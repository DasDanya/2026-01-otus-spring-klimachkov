package ru.klimachkov.workplaces.dsl.predicates;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Component;
import ru.klimachkov.workplaces.dto.reservation.ReservationFilterModel;
import ru.klimachkov.workplaces.models.QReservation;

/**
 * Билдер условий фильтрации бронирований
 */
@Component
public class ReservationPredicateBuilder {

    /**
     * Формирует условия фильтрации бронирований на основе {@link ReservationFilterModel}.
     *
     * <p>Если фильтр {@code null} или все параметры фильтра отсутствуют,
     * возвращается пустое условие без ограничений выборки.</p>
     *
     * @param filterModel модель с параметрами фильтрации бронирований
     * @return {@link Predicate} с условиями фильтрации
     *
     * @see Predicate
     * @see ReservationFilterModel
     * @see QReservation
     */
    public Predicate build(ReservationFilterModel filterModel) {
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        if (filterModel != null) {
            QReservation queryReservation = QReservation.reservation;

            addWorkplaceFilters(queryReservation, predicateBuilder, filterModel);
            addEmployeeFilters(queryReservation, predicateBuilder, filterModel);
            addTimeFilters(queryReservation, predicateBuilder, filterModel);
        }

        return predicateBuilder;
    }

    /**
     * Добавляет условия фильтрации по параметрам рабочего места
     *
     * @param queryReservation Q-объект бронирования для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации бронирований
     */
    private void addWorkplaceFilters(
            QReservation queryReservation,
            BooleanBuilder predicateBuilder,
            ReservationFilterModel filterModel
    ) {
        if (filterModel.workplaceId() != null) {
            predicateBuilder.and(queryReservation.workplace.id.eq(filterModel.workplaceId()));
        }

        if (filterModel.workplaceName() != null && !filterModel.workplaceName().isBlank()) {
            predicateBuilder.and(queryReservation.workplace.name.containsIgnoreCase(filterModel.workplaceName()));
        }

        if (filterModel.workplaceType() != null) {
            predicateBuilder.and(queryReservation.workplace.type.eq(filterModel.workplaceType()));
        }
    }

    /**
     * Добавляет условия фильтрации по параметрам сотрудника
     *
     * @param queryReservation Q-объект бронирования для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации бронирований
     */
    private void addEmployeeFilters(
            QReservation queryReservation,
            BooleanBuilder predicateBuilder,
            ReservationFilterModel filterModel
    ) {
        if (filterModel.employeeId() != null) {
            predicateBuilder.and(queryReservation.employee.id.eq(filterModel.employeeId()));
        }

        if (filterModel.employeeLastName() != null && !filterModel.employeeLastName().isBlank()) {
            predicateBuilder.and(queryReservation.employee.lastname.containsIgnoreCase(filterModel.employeeLastName()));
        }
    }

    /**
     * Добавляет условия фильтрации по параметрам временного диапазона бронирования.
     *
     * <p>Если указан общий диапазон {@code rangeFrom} и {@code rangeTo},
     * выполняется поиск бронирований, пересекающихся с данным временным интервалом.
     * В противном случае применяется фильтрация по отдельным параметрам начала
     * и окончания бронирования.</p>
     *
     * @param queryReservation Q-объект бронирования для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации бронирований
     */
    private void addTimeFilters(
            QReservation queryReservation,
            BooleanBuilder predicateBuilder,
            ReservationFilterModel filterModel
    ) {
        if (filterModel.rangeFrom() != null && filterModel.rangeTo() != null) {
            predicateBuilder.and(queryReservation.startTime.loe(filterModel.rangeTo()))
                    .and(queryReservation.endTime.goe(filterModel.rangeFrom()));
        } else {
            addStartEndTimeFilters(queryReservation, predicateBuilder, filterModel);
        }
    }

    /**
     * Добавляет условия фильтрации по отдельным параметрам времени начала и окончания бронирования.
     *
     * <p>Поддерживается фильтрация по:
     * <ul>
     *     <li>началу бронирования (от и до указанного времени)</li>
     *     <li>окончанию бронирования (от и до указанного времени)</li>
     * </ul>
     * </p>
     *
     * @param queryReservation Q-объект бронирования для построения условий
     * @param predicateBuilder билдер условий фильтрации
     * @param filterModel модель с параметрами фильтрации бронирований
     */
    private void addStartEndTimeFilters(
            QReservation queryReservation,
            BooleanBuilder predicateBuilder,
            ReservationFilterModel filterModel
    ) {
        // startTime >= startTimeFrom
        if (filterModel.startTimeFrom() != null) {
            predicateBuilder.and(queryReservation.startTime.goe(filterModel.startTimeFrom()));
        }

        // startTime <= startTimeTo
        if (filterModel.startTimeTo() != null) {
            predicateBuilder.and(queryReservation.startTime.loe(filterModel.startTimeTo()));
        }

        // endTime >= endTimeFrom
        if (filterModel.endTimeFrom() != null) {
            predicateBuilder.and(queryReservation.endTime.goe(filterModel.endTimeFrom()));
        }

        // endTime <= endTimeTo
        if (filterModel.endTimeTo() != null) {
            predicateBuilder.and(queryReservation.endTime.loe(filterModel.endTimeTo()));
        }
    }
}
