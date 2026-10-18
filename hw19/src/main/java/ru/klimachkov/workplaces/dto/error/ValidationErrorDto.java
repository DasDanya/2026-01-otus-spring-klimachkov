package ru.klimachkov.workplaces.dto.error;

/**
 * DTO с информацией об отдельной ошибке валидации поля.
 *
 * <p>Содержит код ошибки валидации и сообщение,
 * описывающее причину, по которой значение поля не прошло проверку.</p>
 *
 * @param code код ошибки валидации
 * @param message сообщение об ошибке валидации
 */
public record ValidationErrorDto(String code, String message) {
}
