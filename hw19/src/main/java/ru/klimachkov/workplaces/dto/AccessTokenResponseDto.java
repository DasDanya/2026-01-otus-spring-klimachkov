package ru.klimachkov.workplaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с новым JWT access token")
public record AccessTokenResponseDto(
        @Schema(
                description = "Новый JWT access token для доступа к защищённым эндпоинтам API",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvZmZpY2VfYWRtaW5pc3RyYXRvckBleGFtcGxlLmNvbSIsInRva2VuVH"
        )
        String accessToken
) {
}
