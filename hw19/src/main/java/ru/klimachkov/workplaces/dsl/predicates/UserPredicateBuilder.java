package ru.klimachkov.workplaces.dsl.predicates;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Component;
import ru.klimachkov.workplaces.dto.user.UserFilterModel;
import ru.klimachkov.workplaces.models.QUser;

/**
 * Билдер условий фильтрации пользователей
 */
@Component
public class UserPredicateBuilder {

    /**
     * Формирует условия фильтрации пользователей на основе {@link UserFilterModel}.
     *
     * <p>Если фильтр {@code null} или все параметры фильтра отсутствуют,
     * возвращается пустое условие без ограничений выборки.</p>
     *
     * @param filterModel модель с параметрами фильтрации пользователей
     * @return {@link Predicate} с условиями фильтрации
     *
     * @see Predicate
     * @see UserFilterModel
     * @see QUser
     */
    public Predicate build(UserFilterModel filterModel) {
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        if (filterModel != null) {
            QUser queryUser = QUser.user;

            if (filterModel.name() != null && !filterModel.name().isBlank()) {
                predicateBuilder.and(queryUser.name.containsIgnoreCase(filterModel.name()));
            }

            if (filterModel.email() != null && !filterModel.email().isBlank()) {
                predicateBuilder.and(queryUser.email.containsIgnoreCase(filterModel.email()));
            }
        }

        return predicateBuilder;
    }
}
