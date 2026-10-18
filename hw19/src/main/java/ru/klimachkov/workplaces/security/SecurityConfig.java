package ru.klimachkov.workplaces.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import ru.klimachkov.workplaces.models.UserRole;
import ru.klimachkov.workplaces.security.jwt.JwtAuthenticationFilter;

/**
 * Конфигурация безопасности приложения.
 *
 * <p>Настраивает механизм аутентификации и авторизации пользователей
 * с использованием Spring Security и JWT-аутентификации.</p>
 *
 * <p>Определяет правила доступа к API на основе ролей пользователей,
 * настраивает обработку ошибок безопасности, отключает состояние сессии
 * и добавляет JWT-фильтр для обработки защищенных запросов.</p>
 *
 * <p>Также отвечает за настройку компонентов, необходимых для процесса
 * аутентификации: {@link PasswordEncoder}, {@link AuthenticationProvider}
 * и {@link AuthenticationManager}.</p>
 *
 * @see JwtAuthenticationFilter
 * @see UserRole
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Фильтр для извлечения и проверки JWT-токенов при выполнении HTTP-запросов.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Обработчик выхода пользователя из системы.
     */
    private final LogoutHandler logoutHandler;

    /**
     * Обработчик ошибок доступа к защищенным ресурсам.
     */
    private final AccessDeniedHandler accessDeniedHandler;

    /**
     * Обработчик попыток доступа без аутентификации.
     */
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Сервис загрузки данных пользователей для Spring Security.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Создает кодировщик паролей на основе алгоритма BCrypt.
     *
     * @return компонент для кодирования и проверки паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает провайдер аутентификации пользователей.
     *
     * <p>Использует {@link UserDetailsService} для получения данных пользователя
     * и {@link PasswordEncoder} для проверки пароля.</p>
     *
     * @return настроенный провайдер аутентификации
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Предоставляет менеджер аутентификации Spring Security.
     *
     * @param configuration конфигурация аутентификации Spring Security
     * @return менеджер аутентификации
     * @throws Exception если не удалось получить менеджер аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Настраивает цепочку фильтров безопасности приложения.
     *
     * <p>Определяет основные параметры безопасности:
     * отключение CSRF, использование stateless-сессий,
     * подключение JWT-фильтра, настройку logout и обработчиков ошибок.</p>
     *
     * @param http конфигурация HTTP-безопасности Spring Security
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если возникает ошибка конфигурации безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(this::authorizeHttpRequests)
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) ->
                                SecurityContextHolder.clearContext()))
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    /**
     * Настраивает правила доступа к HTTP-ресурсам приложения.
     *
     * <p>Определяет публичные и защищенные endpoint'ы,
     * а также необходимые роли пользователей для выполнения операций.</p>
     *
     * @param authorize реестр правил авторизации HTTP-запросов
     */
    private void authorizeHttpRequests(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize
    ) {
        authorize.requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/users/**").hasRole(UserRole.SYSTEM_ADMIN.name())
                .requestMatchers(HttpMethod.POST,"/api/employees", "/api/locations", "/api/workplaces")
                    .hasAnyRole(UserRole.SYSTEM_ADMIN.name(), UserRole.OFFICE_ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/locations/{id}")
                    .hasAnyRole(UserRole.SYSTEM_ADMIN.name(), UserRole.OFFICE_ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, "/api/employees/{id}")
                    .hasAnyRole(UserRole.SYSTEM_ADMIN.name(), UserRole.OFFICE_ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/employees/{id}", "/api/locations/{id}","/api/workplaces/{id}")
                    .hasAnyRole(UserRole.SYSTEM_ADMIN.name(), UserRole.OFFICE_ADMIN.name())
                .requestMatchers(
                        "/api/workplaces/{id}/rename", "/api/workplaces/{id}/to-hot-desk",
                        "/api/workplaces/{id}/to-individual", "/api/workplaces/{id}/replace-employee",
                        "/api/workplaces/location/{locationId}", "/api/workplaces/employee/{employeeId}",
                        "/api/reservations/employee/{employeeId}", "/api/reservations/workplace/{workplaceId}")
                    .hasAnyRole(UserRole.SYSTEM_ADMIN.name(), UserRole.OFFICE_ADMIN.name())
                .anyRequest().authenticated();
    }
}
