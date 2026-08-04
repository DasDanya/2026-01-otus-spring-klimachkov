package ru.klimachkov.workplaces.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.models.User;

/**
 * Маппер для преобразования сущности пользователя в DTO представление.
 *
 * <p>Используется для преобразования объектов {@link User} в {@link UserResponseDto}</p>
 *
 * <p>Реализация маппера генерируется библиотекой MapStruct и регистрируется в Spring-контексте как компонент.</p>
 *
 * @see User
 * @see UserResponseDto
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Преобразует сущность пользователя в DTO представление.
     *
     * @param user сущность пользователя
     * @return DTO пользователя для передачи клиенту
     */
    UserResponseDto toResponseDto(User user);
}
