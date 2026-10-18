package ru.klimachkov.workplaces.util;


import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.klimachkov.workplaces.exceptions.security.UnauthenticatedException;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.models.UserRole;
import ru.klimachkov.workplaces.security.CustomUserDetails;

/**
 * Утилитный класс для работы с данными пользователя.
 *
 * @see SecurityContextHolder
 * @see CustomUserDetails
 * @see UserRole
 */
public class UserUtils {

    /**
     * Возвращает текущего аутентифицированного пользователя.
     *
     * <p>Получение пользователя выполняется через контекст безопасности Spring Security.</p>
     *
     * @return текущий пользователь системы
     * @throws UnauthenticatedException если пользователь не аутентифицирован
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw new UnauthenticatedException("User not authenticated");
        }

        return customUserDetails.user();
    }

    /**
     * Проверяет, является ли пользователь офисным администратором или системным.
     *
     * @param user пользователь системы
     * @return {@code true}, если пользователь имеет роль
     * {@link UserRole#SYSTEM_ADMIN} или {@link UserRole#OFFICE_ADMIN},
     * иначе {@code false}
     */
    public static boolean isAdmin(@NonNull User user) {
        return user.getRole() == UserRole.SYSTEM_ADMIN || user.getRole() == UserRole.OFFICE_ADMIN;
    }

    /**
     * Проверяет, является ли пользователь системным администратором.
     *
     * @param user пользователь системы
     * @return {@code true}, если пользователь имеет роль
     * {@link UserRole#SYSTEM_ADMIN}, иначе {@code false}
     */
    public static boolean isSystemAdmin(@NonNull User user) {
        return user.getRole() == UserRole.SYSTEM_ADMIN;
    }
}
