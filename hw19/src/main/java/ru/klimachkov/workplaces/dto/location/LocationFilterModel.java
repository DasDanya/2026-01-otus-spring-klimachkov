package ru.klimachkov.workplaces.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Параметры фильтрации локаций")
public record LocationFilterModel(@Schema(description = "Название локации", example = "Отдел продаж") String name) {
}
