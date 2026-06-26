package ru.klimachkov.hw.dto;

import java.util.List;

public record FieldErrorDto(String field, List<String> errors) {
}
