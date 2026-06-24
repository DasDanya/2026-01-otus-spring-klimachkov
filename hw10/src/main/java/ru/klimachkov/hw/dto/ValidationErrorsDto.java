package ru.klimachkov.hw.dto;

import java.util.List;

public record ValidationErrorsDto(List<FieldErrorDto> errors) {
}
