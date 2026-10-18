package ru.klimachkov.workplaces.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.workplaces.models.RefreshToken;
import ru.klimachkov.workplaces.models.User;
import ru.klimachkov.workplaces.repositories.RefreshTokenRepository;
import ru.klimachkov.workplaces.util.SHA;

import java.util.Optional;

/**
 * Реализация сервиса {@link RefreshTokenService} для работы с refresh-токенами пользователей.
 *
 * @see RefreshTokenRepository
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    /**
     * Репозиторий для работы с refresh-токенами
     */
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Выполняет поиск refresh-токена по его значению.
     *
     * <p>Перед выполнением поиска значение токена преобразуется в хешированное представление.</p>
     *
     * @param token значение refresh-токена
     * @return {@link Optional} с найденным refresh-токеном, если токен существует
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(hashToken(token));
    }

    /**
     * Сохраняет refresh-токен пользователя.
     *
     * <p>Перед сохранением значение токена преобразуется в хешированное представление.</p>
     *
     * @param user пользователь, которому принадлежит токен
     * @param token значение refresh-токена
     * @return сохраненный refresh-токен
     */
    @Override
    @Transactional
    public RefreshToken save(User user, String token) {
        return refreshTokenRepository.save(build(user, token));
    }

    /**
     * Удаляет refresh-токен по его значению.
     *
     * <p>Перед выполнением удаления значение токена преобразуется в хешированное представление.</p>
     *
     * @param token значение refresh-токена
     */
    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(hashToken(token));
    }

    /**
     * Удаляет все refresh-токены указанного пользователя.
     *
     * @param user пользователь, чьи токены необходимо удалить
     */
    @Override
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    /**
     * Создает объект refresh-токена.
     *
     * @param user пользователь, которому принадлежит токен
     * @param refreshToken значение refresh-токена
     * @return объект refresh-токена
     */
    private RefreshToken build(User user, String refreshToken) {
        return RefreshToken.builder()
                .user(user)
                .token(hashToken(refreshToken))
                .build();
    }

    /**
     * Преобразует значение токена в хешированное представление.
     *
     * @param token значение refresh-токена
     * @return SHA-256 хеш токена
     */
    private String hashToken(String token) {
        return SHA.hashSha256(token);
    }
}
