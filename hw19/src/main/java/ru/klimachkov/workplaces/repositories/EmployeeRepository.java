package ru.klimachkov.workplaces.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.klimachkov.workplaces.models.Employee;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link Employee}.
 *
 * <p>Предоставляет методы для выполнения стандартных операций с сотрудниками,
 * а также дополнительные операции поиска</p>
 *
 * @see Employee
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long>, QuerydslPredicateExecutor<Employee> {

    /**
     * Проверяет наличие сотрудника, связанного с указанным пользователем.
     *
     * @param userId ID пользователя
     * @return {@code true}, если сотрудник с указанным пользователем существует,
     * иначе {@code false}
     */
    boolean existsByUserId(Long userId);

    /**
     * Возвращает идентификатор сотрудника, связанного с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return {@link Optional} с идентификатором сотрудника,
     * если связь с пользователем существует
     */
    @Query("SELECT e.id FROM Employee e WHERE e.user.id = :userId")
    Optional<Long> findIdByUserId(@Param("userId") Long userId);
}
