package ru.klimachkov.workplaces.security;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.klimachkov.workplaces.models.User;

import java.util.Collection;
import java.util.Collections;

/**
 * Реализация {@link UserDetails} для интеграции сущности {@link User}
 * с механизмом аутентификации Spring Security.
 *
 * <p>Адаптирует данные пользователя системы под формат,
 * необходимый Spring Security для выполнения аутентификации
 * и проверки прав доступа.</p>
 *
 * @param user сущность пользователя системы
 *
 * @see User
 * @see UserDetails
 */
public record CustomUserDetails(@NonNull User user) implements UserDetails {

    /**
     * Возвращает имя пользователя для Spring Security.
     *
     * <p>В качестве имени пользователя используется email.</p>
     *
     * @return email пользователя
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return закодированный пароль пользователя
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает полномочия пользователя.
     *
     * <p>Роль пользователя преобразуется в формат Spring Security
     * с добавлением префикса {@code ROLE_}.</p>
     *
     * @return коллекция полномочий пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    /**
     * Проверяет, заблокирована ли учетная запись пользователя.
     *
     * @return {@code true}, если учетная запись не заблокирована,
     * иначе {@code false}
     */
    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }
}
