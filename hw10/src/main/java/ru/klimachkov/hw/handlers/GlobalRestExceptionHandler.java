package ru.klimachkov.hw.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.klimachkov.hw.dto.FieldErrorDto;
import ru.klimachkov.hw.dto.ValidationErrorsDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "ru.klimachkov.hw.controllers.rest")
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorsDto handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<FieldErrorDto> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                error -> error.getDefaultMessage() != null
                                        ? error.getDefaultMessage()
                                        : "Validation error",
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> new FieldErrorDto(entry.getKey(), entry.getValue()))
                .toList();

        return new ValidationErrorsDto(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFound(EntityNotFoundException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", exception.getMessage());
        return error;
    }
}
