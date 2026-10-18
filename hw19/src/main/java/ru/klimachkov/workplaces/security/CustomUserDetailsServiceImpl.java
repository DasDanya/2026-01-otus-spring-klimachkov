package ru.klimachkov.workplaces.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.exceptions.UserNotFoundException;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.services.UserService;

/**
 * Реализация сервиса загрузки данных пользователя для Spring Security.
 *
 * <p>Используется механизмом аутентификации Spring Security для получения
 *  информации о пользователе по его {@code username}.</p>
 *
 * @see UserDetailsService
 * @see CustomUserDetails
 * @see UserService
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    /**
     * Сервис для работы с пользователями системы
     */
    private final UserService userService;

    /**
     * Загружает данные пользователя по email.
     *
     * <p>Если пользователь найден, возвращает его представление
     * в виде {@link CustomUserDetails}. В случае отсутствия пользователя
     * преобразует бизнес-исключение в {@link UsernameNotFoundException},
     * требуемое контрактом Spring Security.</p>
     *
     * @param email email пользователя
     * @return данные пользователя для Spring Security
     * @throws UsernameNotFoundException если пользователь с указанным email
     * не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userService.findByEmail(email);
            return new CustomUserDetails(user);
        } catch (UserNotFoundException ex) {
            throw new UsernameNotFoundException("User not found", ex);
        }
    }
}
