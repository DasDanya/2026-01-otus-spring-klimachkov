package ru.klimachkov.workplaces.services;

import ru.klimachkov.workplaces.models.RefreshToken;
import ru.klimachkov.workplaces.models.User;

import java.util.Optional;

/**
 * Сервис для работы с refresh-токенами пользователей.
 *
 * <p>Предоставляет операции для поиска, сохранения и удаления
 * refresh-токенов, связанных с пользователями.</p>
 */
public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken save(User user, String token);

    void deleteByToken(String token);

    void deleteByUser(User user);
}
