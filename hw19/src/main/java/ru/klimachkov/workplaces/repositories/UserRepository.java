package ru.klimachkov.workplaces.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.klimachkov.workplaces.models.User;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link User}.
 *
 * <p>Предоставляет методы для выполнения стандартных операций с пользователями,
 * а также дополнительные операции поиска и проверки существования пользователя</p>
 *
 * @see User
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 */
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    /**
     * Проверяет наличие пользователя с указанным адресом электронной почты.
     *
     * <p>Проверка выполняется без учета регистра.</p>
     *
     * @param email адрес электронной почты пользователя
     * @return {@code true}, если пользователь с указанным email существует,
     * иначе {@code false}
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Выполняет поиск пользователя по адресу электронной почты.
     *
     * <p>Поиск выполняется без учета регистра.</p>
     *
     * @param email адрес электронной почты пользователя
     * @return {@link Optional} с найденным пользователем,
     * если пользователь с указанным email существует
     */
    Optional<User> findByEmailIgnoreCase(String email);
}
