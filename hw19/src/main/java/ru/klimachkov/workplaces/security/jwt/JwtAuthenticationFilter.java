package ru.klimachkov.workplaces.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр аутентификации пользователей с использованием JWT-токена.
 *
 * <p>Извлекает JWT-токен из HTTP-запроса, выполняет его проверку
 * и при успешной валидации устанавливает информацию о пользователе
 * в {@link SecurityContextHolder}.</p>
 *
 * <p>Фильтр выполняется один раз для каждого HTTP-запроса
 * в рамках цепочки фильтров Spring Security.</p>
 *
 * @see JwtService
 * @see UserDetailsService
 * @see SecurityContextHolder
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    /**
     * Обрабатывает входящий HTTP-запрос и выполняет JWT-аутентификацию.
     *
     * <p>Если запрос содержит JWT-токен, выполняется его проверка,
     * после чего данные пользователя устанавливаются в контекст безопасности.</p>
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param filterChain цепочка фильтров Spring Security
     * @throws ServletException если возникает ошибка обработки запроса
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtService.extractToken(request);
        if (token != null) {
            setUserDetailsToSecurityContextHolder(request, token);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Устанавливает данные аутентифицированного пользователя в {@link SecurityContextHolder}.
     *
     * <p>Метод извлекает имя пользователя из JWT-токена, загружает
     * его данные и устанавливает аутентификацию при успешной проверке токена.</p>
     *
     * @param request HTTP-запрос
     * @param token JWT-токен
     */
    private void setUserDetailsToSecurityContextHolder(HttpServletRequest request, String token) {
        String username = jwtService.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
}
