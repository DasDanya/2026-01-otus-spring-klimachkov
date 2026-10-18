package ru.klimachkov.workplaces.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.dto.user.UserFilterModel;
import ru.klimachkov.workplaces.dto.user.UserResponseDto;
import ru.klimachkov.workplaces.exceptions.UserNotFoundException;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.models.UserRole;

/**
 * Сервис для работы с пользователями.
 *
 * <p>Предоставляет операции для получения, создания и изменения данных пользователей,
 * а также поиска с учетом фильтрации и пагинации. Дополнительно поддерживает управление
 * ролями и состоянием учетной записи пользователей.</p>
 */
public interface UserService {

    @NonNull
    User findById(Long id);

    @NonNull
    UserResponseDto getDtoById(Long id);

    @NonNull
    User getMe();

    @NonNull
    UserResponseDto getMeDto();

    PagedModel<UserResponseDto> findAll(UserFilterModel userFilterModel, Pageable pageable);

    boolean existsByEmail(String email);

    @NonNull
    User findByEmail(String email) throws UserNotFoundException;

    User insert(String name, String email, String password);

    UserResponseDto changeRole(Long id, UserRole userRole);

    void lock(Long id);

    void unlock(Long id);
}
