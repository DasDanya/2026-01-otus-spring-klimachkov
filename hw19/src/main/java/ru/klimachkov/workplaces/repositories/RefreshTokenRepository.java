package ru.klimachkov.workplaces.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.klimachkov.workplaces.models.RefreshToken;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link RefreshToken}.
 *
 * <p>Предоставляет методы для выполнения стандартных операций с токенами,
 * а также дополнительные методы поиска и удаления refresh-токенов</p>
 *
 * @see RefreshToken
 * @see CrudRepository
 */
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    /**
     * Выполняет поиск refresh-токена по его хешированному значению.
     *
     * <p>В системе хранятся только хешированные значения refresh-токенов,
     * поэтому переданное значение должно быть предварительно преобразовано
     * в соответствующий хеш.</p>
     *
     * @param token хешированное значение refresh-токена
     * @return {@link Optional} с найденным токеном,
     * если токен с указанным хешем существует
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Удаляет refresh-токен по его хешированному значению.
     *
     * <p>Удаление выполняется по хешу токена, так как исходное значение
     * refresh-токена не хранится в системе.</p>
     *
     * @param token хешированное значение refresh-токена
     */
    void deleteByToken(String token);

    /**
     * Удаляет все refresh-токены, принадлежащие указанному пользователю.
     *
     * @param userId идентификатор пользователя
     */
    void deleteByUserId(Long userId);

}
