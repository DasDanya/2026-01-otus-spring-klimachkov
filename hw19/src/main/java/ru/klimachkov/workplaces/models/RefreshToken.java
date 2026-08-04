package ru.klimachkov.workplaces.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Сущность refresh-токена пользователя.
 *
 * <p>Хранит данные о refresh-токенах, используемых для обновления access-токенов.</p>
 *
 * <p>Один пользователь может иметь несколько активных refresh-токенов.</p>
 *
 * @see User
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    /**
     * ID refresh-токена
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Refresh-токен
     */
    @Column(name = "token", nullable = false)
    private String token;

    /**
     * Пользователь, которому принадлежит refresh-токен
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
