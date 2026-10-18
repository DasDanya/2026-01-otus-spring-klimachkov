package ru.klimachkov.workplaces.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Обработчик ошибок доступа к защищенным ресурсам.
 *
 * <p>Формирует HTTP-ответ с кодом {@link HttpStatus#FORBIDDEN}
 * при попытке выполнения операции пользователем, у которого недостаточно прав доступа.</p>
 *
 * <p>Возвращает информацию об ошибке в формате JSON с использованием {@link ProblemDetail}.</p>
 *
 * @see AccessDeniedHandler
 * @see ObjectMapper
 */
@Component
@RequiredArgsConstructor
public class GlobalAccessDeniedHandler implements AccessDeniedHandler {

    /**
     *  Маппер для сериализации данных ответа в JSON
     */
    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает ситуацию отсутствия прав доступа у пользователя.
     *
     * <p>Создает описание ошибки со статусом {@link HttpStatus#FORBIDDEN},
     * устанавливает параметры HTTP-ответа и записывает описание ошибки в формате JSON.</p>
     *
     * @param request HTTP-запрос, вызвавший ошибку доступа
     * @param response HTTP-ответ с информацией об ошибке
     * @param accessDeniedException исключение, возникшее при отказе в доступе
     *
     * @throws IOException если произошла ошибка при записи ответа
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problemDetail.setTitle("Access denied");
        problemDetail.setDetail("Access is denied. Insufficient permissions to perform this action");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
