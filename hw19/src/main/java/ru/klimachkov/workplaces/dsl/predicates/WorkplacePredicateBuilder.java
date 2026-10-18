package ru.klimachkov.workplaces.dsl.predicates;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Component;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceFilterModel;
import ru.klimachkov.workplaces.models.QWorkplace;

/**
 * Билдер условий фильтрации рабочих мест
 */
@Component
public class WorkplacePredicateBuilder {

    /**
     * Формирует условия фильтрации рабочих мест на основе {@link WorkplaceFilterModel}.
     *
     * <p>Если фильтр {@code null} или все параметры фильтра отсутствуют,
     * возвращается пустое условие без ограничений выборки.</p>
     *
     * @param filterModel модель с параметрами фильтрации рабочих мест
     * @return {@link Predicate} с условиями фильтрации
     *
     * @see Predicate
     * @see WorkplaceFilterModel
     * @see QWorkplace
     */
    public Predicate build(WorkplaceFilterModel filterModel) {
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        if (filterModel != null) {
            QWorkplace queryWorkplace = QWorkplace.workplace;

            addWorkplaceFilters(queryWorkplace, predicateBuilder, filterModel);
            addLocationFilters(queryWorkplace, predicateBuilder, filterModel);
            addEmployeeFilters(queryWorkplace, predicateBuilder, filterModel);
        }

        return predicateBuilder;
    }

    /**
     * Добавляет условия фильтрации по параметрам рабочего места
     *
     * @param queryWorkplace Q-объект рабочего места для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации рабочих мест
     */
    private void addWorkplaceFilters(
            QWorkplace queryWorkplace,
            BooleanBuilder predicateBuilder,
            WorkplaceFilterModel filterModel
    ) {
        if (filterModel.name() != null && !filterModel.name().isBlank()) {
            predicateBuilder.and(queryWorkplace.name.containsIgnoreCase(filterModel.name()));
        }

        if (filterModel.type() != null) {
            predicateBuilder.and(queryWorkplace.type.eq(filterModel.type()));
        }
    }

    /**
     * Добавляет условия фильтрации по параметрам локации
     *
     * @param queryWorkplace Q-объект рабочего места для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации рабочих мест
     */
    private void addLocationFilters(
            QWorkplace queryWorkplace,
            BooleanBuilder predicateBuilder,
            WorkplaceFilterModel filterModel
    ) {
        if (filterModel.locationId() != null) {
            predicateBuilder.and(queryWorkplace.location.id.eq(filterModel.locationId()));
        }

        if (filterModel.locationName() != null && !filterModel.locationName().isBlank()) {
            predicateBuilder.and(queryWorkplace.location.name.containsIgnoreCase(filterModel.locationName()));
        }
    }

    /**
     * Добавляет условия фильтрации по параметрам сотрудника, за которым закреплено рабочее место
     *
     * @param queryWorkplace Q-объект рабочего места для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации рабочих мест
     */
    private void addEmployeeFilters(
            QWorkplace queryWorkplace,
            BooleanBuilder predicateBuilder,
            WorkplaceFilterModel filterModel
    ) {
        if (filterModel.employeeId() != null) {
            predicateBuilder.and(queryWorkplace.employee.id.eq(filterModel.employeeId()));
        }

        if (filterModel.employeeLastName() != null && !filterModel.employeeLastName().isBlank()) {
            predicateBuilder.and(queryWorkplace.employee.lastname.containsIgnoreCase(filterModel.employeeLastName()));
        }
    }
}
