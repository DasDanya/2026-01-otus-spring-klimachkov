package ru.klimachkov.workplaces.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.klimachkov.workplaces.models.Location;

/**
 * Репозиторий для работы с сущностями {@link Location}.
 *
 * <p>Предоставляет методы для выполнения стандартных операций с локациями,
 * а также дополнительные операции поиска и проверки существования локаций</p>
 *
 * @see Location
 * @see JpaRepository
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Выполняет поиск локаций по части названия без учета регистра.
     *
     * @param name часть названия локации для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница локаций, соответствующих критерию поиска
     */
    Page<Location> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Проверяет существование локации с указанным названием.
     *
     * @param name название локации
     * @return {@code true}, если локация с таким названием существует,
     * иначе {@code false}
     */
    boolean existsByName(String name);

    /**
     * Проверяет существование другой локации с указанным названием.
     *
     * <p>Используется для проверки уникальности названия без учета текущей локации.</p>
     *
     * @param name название локации
     * @param id идентификатор локации, которую необходимо исключить из проверки
     * @return {@code true}, если существует другая локация с таким названием,
     * иначе {@code false}
     */
    boolean existsByNameAndIdNot(String name, Long id);

}
