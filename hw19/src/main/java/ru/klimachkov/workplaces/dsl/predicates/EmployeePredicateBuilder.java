package ru.klimachkov.workplaces.dsl.predicates;

import org.springframework.stereotype.Component;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import ru.klimachkov.workplaces.dto.employee.EmployeeFilterModel;
import ru.klimachkov.workplaces.models.QEmployee;

/**
 * Билдер условий фильтрации сотрудников
 */
@Component
public class EmployeePredicateBuilder {

    /**
     * Формирует условия фильтрации сотрудников на основе {@link EmployeeFilterModel}.
     *
     * <p>Если фильтр {@code null} или все параметры фильтра отсутствуют,
     * возвращается пустое условие без ограничений выборки.</p>
     *
     * @param filterModel модель с параметрами фильтрации сотрудников
     * @return {@link Predicate} с условиями фильтрации
     *
     * @see Predicate
     * @see EmployeeFilterModel
     * @see QEmployee
     */
    public Predicate build(EmployeeFilterModel filterModel) {
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        if (filterModel != null) {
            QEmployee queryEmployee = QEmployee.employee;

            if (filterModel.firstname() != null && !filterModel.firstname().isBlank()) {
                predicateBuilder.and(queryEmployee.firstname.containsIgnoreCase(filterModel.firstname()));
            }

            if (filterModel.lastname() != null && !filterModel.lastname().isBlank()) {
                predicateBuilder.and(queryEmployee.lastname.containsIgnoreCase(filterModel.lastname()));
            }

            if (filterModel.middlename() != null && !filterModel.middlename().isBlank()) {
                predicateBuilder.and(queryEmployee.middlename.containsIgnoreCase(filterModel.middlename()));
            }

            if (filterModel.userId() != null) {
                predicateBuilder.and(queryEmployee.user.id.eq(filterModel.userId()));
            }
        }

        return predicateBuilder;
    }
}
