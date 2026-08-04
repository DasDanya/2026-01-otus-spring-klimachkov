package ru.klimachkov.workplaces.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Обработчик ошибок аутентификации.
 *
 * <p>Используется Spring Security для обработки ситуаций, когда запрос выполняется без необходимой аутентификации.</p>
 *
 * @see AuthenticationEntryPoint
 * @see ProblemDetail
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     *  Маппер для сериализации данных ответа в JSON
     */
    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает попытку доступа к защищенному ресурсу без успешной аутентификации.
     *
     * <p>Создает описание ошибки {@link ProblemDetail}
     * со статусом {@link HttpStatus#UNAUTHORIZED} и возвращает
     * его клиенту в формате JSON.</p>
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param authenticationException исключение, возникшее при аутентификации
     * @throws IOException если возникает ошибка записи ответа
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problemDetail.setTitle("Unauthorized");
        problemDetail.setDetail("Authentication required");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}