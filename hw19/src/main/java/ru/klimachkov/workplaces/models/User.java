package ru.klimachkov.workplaces.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность пользователя системы.
 *
 * <p>Представляет учетную запись, используемую для аутентификации
 * и авторизации в системе.</p>
 *
 * <p>Пользователь содержит основные учетные данные, включая email и пароль,
 * а также роль ({@link UserRole}), определяющую уровень доступа.</p>
 *
 * <p>Поле {@code locked} указывает, заблокирована ли учетная запись пользователя.</p>
 *
 * @see UserRole
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    /**
     * ID пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Наименование пользователя
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Email пользователя
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Пароль пользователя (в зашифрованном виде)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Роль пользователя
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    /**
     * Признак блокировки учетной записи
     */
    @Column(name = "locked", nullable = false)
    private boolean locked;

}
