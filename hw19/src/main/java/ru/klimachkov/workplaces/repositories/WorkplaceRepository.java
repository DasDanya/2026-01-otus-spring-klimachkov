package ru.klimachkov.workplaces.repositories;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.models.Workplace;
import ru.klimachkov.workplaces.models.WorkplaceType;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link Workplace}.
 *
 * <p>Предоставляет методы для выполнения стандартных операций с рабочими местами,
 * а также дополнительные операции поиска, проверки бизнес-ограничений и удаления
 * рабочих мест по связанным сущностям.</p>
 *
 * @see Workplace
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 */
public interface WorkplaceRepository extends JpaRepository<Workplace, Long>, QuerydslPredicateExecutor<Workplace> {

    /**
     * Выполняет поиск рабочего места, привязанного за указанным сотрудником.
     *
     * @param employeeId идентификатор сотрудника
     * @return {@link Optional} с найденным рабочим местом,
     * если сотрудник имеет привязанное рабочее место
     */
    Optional<Workplace> findByEmployeeId(Long employeeId);

    /**
     * Выполняет поиск рабочего места по идентификатору
     * с загрузкой связанных сущностей.
     *
     * <p>Вместе с рабочим местом загружаются связанные {@code Location} и {@code Employee}.</p>
     *
     * @param id идентификатор рабочего места
     * @return {@link Optional} с найденным рабочим местом
     */
    @EntityGraph(attributePaths = {"location", "employee"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Workplace> findWithRelationsById(Long id);

    /**
     * Выполняет поиск рабочих мест с учетом QueryDSL-условий
     * и загружает связанные сущности.
     *
     * <p>Используется для получения списка рабочих мест с фильтрацией и пагинацией.</p>
     *
     * <p>Вместе с рабочим местом загружаются связанные {@code Location} и {@code Employee}.</p>
     *
     * @param predicate условия фильтрации
     * @param pageable параметры пагинации и сортировки
     * @return страница рабочих мест с загруженными связями
     */
    @NonNull
    @Override
    @EntityGraph(attributePaths = {"location", "employee"}, type = EntityGraph.EntityGraphType.LOAD)
    Page<Workplace> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable);

    /**
     * Проверяет существование рабочего места с указанным названием в определенной локации.
     *
     * @param name название рабочего места
     * @param locationId идентификатор локации
     * @return {@code true}, если рабочее место с такими параметрами существует,
     * иначе {@code false}
     */
    boolean existsByNameAndLocationId(String name, Long locationId);

    /**
     * Проверяет существование другого рабочего места с указанным названием в определенной локации.
     *
     * <p>Используется для проверки уникальности названия без учета текущего рабочего места.</p>
     *
     * @param name название рабочего места
     * @param locationId идентификатор локации
     * @param id идентификатор рабочего места, исключаемого из проверки
     * @return {@code true}, если существует другое рабочее место
     * с такими параметрами, иначе {@code false}
     */
    boolean existsByNameAndLocationIdAndIdNot(String name, Long locationId, Long id);

    /**
     * Проверяет наличие рабочего места определенного типа, привязанного к указанному сотруднику.
     *
     * @param type тип рабочего места
     * @param employeeId идентификатор сотрудника
     * @return {@code true}, если существует рабочее место с указанным типом,
     * привязанное к сотруднику, иначе {@code false}
     */
    boolean existsByTypeAndEmployeeId(WorkplaceType type, Long employeeId);

    /**
     * Проверяет наличие другого рабочего места определенного типа, привязанного к указанному сотруднику.
     *
     * <p>Используется для проверки наличия рабочего места определенного типа, привязанного к указанному сотруднику,
     * без учета текущего рабочего места.</p>
     *
     * @param type тип рабочего места
     * @param employeeId идентификатор сотрудника
     * @param id идентификатор рабочего места, исключаемого из проверки
     * @return {@code true}, если существует другое рабочее место с указанным типом,
     * привязанное к сотруднику, иначе {@code false}
     */
    boolean existsByTypeAndEmployeeIdAndIdNot(WorkplaceType type, Long employeeId, Long id);

    /**
     * Удаляет все рабочие места, принадлежащие указанной локации.
     *
     * @param locationId идентификатор локации
     */
    void deleteByLocationId(Long locationId);

    /**
     * Удаляет рабочие места, закрепленные за указанным сотрудником.
     *
     * @param employeeId идентификатор сотрудника
     */
    void deleteByEmployeeId(Long employeeId);
}
