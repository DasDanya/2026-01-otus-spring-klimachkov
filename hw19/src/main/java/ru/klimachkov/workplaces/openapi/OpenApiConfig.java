package ru.klimachkov.workplaces.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Конфигурация документации OpenAPI.
 *
 * <p>Определяет основную информацию об API, включая название, версию
 * и описание функциональных возможностей системы.</p>
 *
 * <p>Также настраивает схему безопасности для аутентификации через
 * JWT Bearer-токены, используемую для доступа к защищённым эндпоинтам.</p>
 *
 * @see OpenAPIDefinition
 * @see SecurityScheme
 */
@OpenAPIDefinition(
        info = @Info(
                title = "REST API системы управления и бронирования рабочих мест",
                version = "1.0",
                description = """
                         API предназначен для автоматизации процессов распределения и бронирования рабочих мест
                         в офисной среде
                        
                         Функциональные возможности:
                        
                         - Регистрация пользователей и аутентификация по email и паролю
                         - Предоставление доступа к защищённым эндпоинтам с использованием JWT токенов
                         - Разграничение доступа к эндпоинтам на основе ролей пользователей
                         - Управление сотрудниками, локациями и рабочими местами
                         - Привязка сотрудников к рабочим местам
                         - Бронирование рабочих мест для сотрудников
                        """)

)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
