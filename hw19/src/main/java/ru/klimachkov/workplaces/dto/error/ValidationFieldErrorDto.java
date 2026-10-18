package ru.klimachkov.workplaces.dto.error;

import java.util.List;

/**
 * DTO с информацией об ошибках валидации конкретного поля.
 *
 * <p>Используется для группировки нескольких ошибок,
 * возникших при проверке одного поля объекта.</p>
 *
 * @param field название поля, для которого произошли ошибки валидации
 * @param errors список ошибок валидации данного поля
 */
public record ValidationFieldErrorDto(String field, List<ValidationErrorDto> errors) {
}
