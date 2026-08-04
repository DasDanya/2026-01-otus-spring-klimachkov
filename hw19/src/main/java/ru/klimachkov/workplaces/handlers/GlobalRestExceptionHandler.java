package ru.klimachkov.workplaces.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.klimachkov.workplaces.dto.error.ValidationErrorDto;
import ru.klimachkov.workplaces.dto.error.ValidationFieldErrorDto;
import ru.klimachkov.workplaces.exceptions.EntityAccessDeniedException;
import ru.klimachkov.workplaces.exceptions.ForbiddenOperationException;
import ru.klimachkov.workplaces.exceptions.EntityConflictException;
import ru.klimachkov.workplaces.exceptions.NotFoundEntityException;
import ru.klimachkov.workplaces.exceptions.security.RefreshTokenException;
import ru.klimachkov.workplaces.exceptions.security.UnauthenticatedException;
import ru.klimachkov.workplaces.exceptions.UnprocessableEntityException;
import ru.klimachkov.workplaces.exceptions.security.UserLockedException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений REST-контроллеров.
 *
 * <p>Перехватывает исключения, возникающие во время обработки HTTP-запросов,
 * и преобразует их в стандартизированный формат {@link ProblemDetail}
 * с соответствующим HTTP-статусом и описанием ошибки.</p>
 *
 * <p>Использует {@link MessageSource} для получения локализованных
 * сообщений об ошибках.</p>
 *
 * @see ProblemDetail
 * @see MessageSource
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRestExceptionHandler {

    /**
     * Источник сообщений для получения локализованных описаний ошибок.
     */
    private final MessageSource messageSource;

    /**
     * Обрабатывает ошибки валидации аргументов методов контроллера.
     *
     * <p>Формирует список ошибок валидации для каждого поля
     * и возвращает ответ со статусом {@link HttpStatus#BAD_REQUEST}.</p>
     *
     * @param exception исключение, содержащее ошибки валидации
     * @return описание ошибки валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<ValidationFieldErrorDto> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField))
                .entrySet()
                .stream()
                .map(entry -> new ValidationFieldErrorDto(
                        entry.getKey(),
                        entry.getValue()
                                .stream()
                                .map(error -> new ValidationErrorDto(
                                        error.getCode(),
                                        error.getDefaultMessage()
                                ))
                                .toList()
                ))
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setProperty("fieldErrors", validationErrors);

        return problemDetail;
    }

    /**
     * Обрабатывает ошибку неверных учетных данных пользователя.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#UNAUTHORIZED}</p>
     *
     * @param locale локаль текущего запроса
     * @return описание ошибки аутентификации
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication failed");
        problemDetail.setDetail(messageSource.getMessage("error.bad_credentials", null, locale));
        return problemDetail;
    }

    /**
     * Обрабатывает попытку выполнения защищенной операции пользователем, который не прошел аутентификацию.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#UNAUTHORIZED}</p>
     *
     * @param exception исключение отсутствия аутентификации
     * @return описание ошибки аутентификации
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ProblemDetail handleUnauthenticated(UnauthenticatedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication required");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    /**
     * Обрабатывает ошибки, связанные с refresh-токеном.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#UNAUTHORIZED}</p>
     *
     * @return описание ошибки refresh-токена
     */
    @ExceptionHandler(RefreshTokenException.class)
    public ProblemDetail handleInvalidRefreshToken() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Refresh token error");
        return problemDetail;
    }

    /**
     * Обрабатывает попытку входа пользователя с заблокированной учетной записью.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#UNAUTHORIZED}</p>
     *
     * @param exception исключение блокировки учетной записи
     * @return описание ошибки блокировки аккаунта
     */
    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLocked(LockedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Account is locked");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    /**
     * Обрабатывает ошибки доступа к сущностям.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#FORBIDDEN}</p>
     *
     * @param exception исключение отсутствия доступа к сущности
     * @param locale локаль текущего запроса
     * @return описание ошибки доступа
     */
    @ExceptionHandler(EntityAccessDeniedException.class)
    public ProblemDetail handleEntityAccessDenied(EntityAccessDeniedException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Entity access denied");
        problemDetail.setDetail(exception.getMessage(messageSource, locale));
        return problemDetail;
    }

    /**
     * Обрабатывает запрещенные операции над сущностями.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#FORBIDDEN} </p>
     *
     * @param exception исключение запрещенной операции
     * @param locale локаль текущего запроса
     * @return описание ошибки доступа
     */
    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperation(ForbiddenOperationException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Forbidden operation");
        problemDetail.setDetail(exception.getMessage(messageSource, locale));
        return problemDetail;
    }

    /**
     * Обрабатывает попытку выполнения операции заблокированным пользователем.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#FORBIDDEN}</p>
     *
     * @param exception исключение блокировки пользователя
     * @return описание ошибки блокировки пользователя
     */
    @ExceptionHandler(UserLockedException.class)
    public ProblemDetail handleUserLocked(UserLockedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("User account is locked");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    /**
     * Обрабатывает ситуации отсутствия запрашиваемой сущности.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#NOT_FOUND}</p>
     *
     * @param exception исключение отсутствия сущности
     * @param locale локаль текущего запроса
     * @return описание ошибки отсутствия сущности
     */
    @ExceptionHandler(NotFoundEntityException.class)
    public ProblemDetail handleNotFoundEntity(NotFoundEntityException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Entity not found");
        problemDetail.setDetail(exception.getMessage(messageSource, locale));
        return problemDetail;
    }

    /**
     * Обрабатывает конфликты данных при выполнении операций.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#CONFLICT}</p>
     *
     * @param exception исключение конфликта сущностей
     * @param locale локаль текущего запроса
     * @return описание ошибки конфликта
     */
    @ExceptionHandler(EntityConflictException.class)
    public ProblemDetail handleEntityConflict(EntityConflictException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Entity conflict");
        problemDetail.setDetail(exception.getMessage(messageSource, locale));
        return problemDetail;
    }

    /**
     * Обрабатывает ошибки некорректного состояния данных.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#UNPROCESSABLE_ENTITY}</p>
     *
     * @param exception исключение некорректной сущности
     * @param locale локаль текущего запроса
     * @return описание ошибки обработки данных
     */
    @ExceptionHandler(UnprocessableEntityException.class)
    public ProblemDetail handleUnprocessableEntity(UnprocessableEntityException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setTitle("Unprocessable entity");
        problemDetail.setDetail(exception.getMessage(messageSource, locale));
        return problemDetail;
    }

    /**
     * Обрабатывает непредвиденные ошибки.
     *
     * <p>Возвращает ответ со статусом {@link HttpStatus#INTERNAL_SERVER_ERROR}</p>
     *
     * @param exception необработанное исключение
     * @return описание внутренней ошибки сервера
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal server error");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}
