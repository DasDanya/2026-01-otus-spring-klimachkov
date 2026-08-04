package ru.klimachkov.workplaces.repositories;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.models.Reservation;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link Reservation}.
 *
 * <p>Предоставляет методы для выполнения стандартных операций с бронированиями,
 * а также дополнительные операции поиска, проверки пересечений бронирований
 * и удаления бронирований по связанным сущностям.</p>
 *
 * @see Reservation
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        QuerydslPredicateExecutor<Reservation> {

    /**
     * Выполняет поиск бронирований с учетом QueryDSL-условий
     * и загружает связанные сущности.
     *
     * <p>Вместе с бронированием загружаются связанные {@code Employee}, {@code Workplace} и {@code Location}.</p>
     *
     * @param predicate условия фильтрации
     * @param pageable параметры пагинации и сортировки
     * @return страница бронирований с загруженными связями
     */
    @NonNull
    @Override
    @EntityGraph(attributePaths = {"employee", "workplace", "workplace.location"},
            type = EntityGraph.EntityGraphType.LOAD)
    Page<Reservation> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable);

    /**
     * Выполняет поиск бронирования по идентификатору
     * с загрузкой связанных сущностей.
     *
     * <p>Вместе с бронированием загружаются связанные {@code Employee}, {@code Workplace} и {@code Location}.</p>
     *
     * @param id идентификатор бронирования
     * @return {@link Optional} с найденным бронированием
     */
    @EntityGraph(attributePaths = {"employee", "workplace", "workplace.location"},
            type = EntityGraph.EntityGraphType.LOAD)
    Optional<Reservation> findWithRelationsById(Long id);

    /**
     * Выполняет поиск бронирования рабочего места,
     * пересекающегося с указанным временным интервалом.
     *
     * @param workplaceId идентификатор рабочего места
     * @param startTime время начала проверяемого интервала
     * @param endTime время окончания проверяемого интервала
     * @return {@link Optional} с найденным пересекающимся бронированием,
     * если оно существует
     */
    Optional<Reservation> findFirstByWorkplaceIdAndEndTimeGreaterThanAndStartTimeLessThan(
            Long workplaceId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Выполняет поиск пересекающегося бронирования рабочего места
     * с исключением указанного бронирования.
     *
     * @param workplaceId идентификатор рабочего места
     * @param startTime время начала проверяемого интервала
     * @param endTime время окончания проверяемого интервала
     * @param id идентификатор бронирования, исключаемого из проверки
     * @return {@link Optional} с найденным пересекающимся бронированием,
     * если оно существует
     */
    Optional<Reservation> findFirstByWorkplaceIdAndEndTimeGreaterThanAndStartTimeLessThanAndIdNot(
            Long workplaceId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long id
    );

    /**
     * Выполняет поиск бронирования сотрудника,
     * пересекающегося с указанным временным интервалом.
     *
     * @param employeeId идентификатор сотрудника
     * @param startTime время начала проверяемого интервала
     * @param endTime время окончания проверяемого интервала
     * @return {@link Optional} с найденным пересекающимся бронированием,
     * если оно существует
     */
    Optional<Reservation> findFirstByEmployeeIdAndEndTimeGreaterThanAndStartTimeLessThan(
            Long employeeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Выполняет поиск пересекающегося бронирования сотрудника
     * с исключением указанного бронирования.
     *
     * @param employeeId идентификатор сотрудника
     * @param startTime время начала проверяемого интервала
     * @param endTime время окончания проверяемого интервала
     * @param id идентификатор бронирования, исключаемого из проверки
     * @return {@link Optional} с найденным пересекающимся бронированием,
     * если оно существует
     */
    Optional<Reservation> findFirstByEmployeeIdAndEndTimeGreaterThanAndStartTimeLessThanAndIdNot(
            Long employeeId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long id
    );

    /**
     * Удаляет все бронирования, связанные с указанным рабочим местом.
     *
     * @param workplaceId идентификатор рабочего места
     */
    void deleteByWorkplaceId(Long workplaceId);

    /**
     * Удаляет все бронирования, связанные с указанным сотрудником.
     *
     * @param employeeId идентификатор сотрудника
     */
    void deleteByEmployeeId(Long employeeId);
}
