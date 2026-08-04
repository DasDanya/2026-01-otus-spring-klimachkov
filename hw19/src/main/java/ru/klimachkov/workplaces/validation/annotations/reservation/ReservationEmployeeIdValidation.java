package ru.klimachkov.workplaces.validation.annotations.reservation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация валидации идентификатора сотрудника при бронировании.
 *
 * <p>Используется для проверки корректности идентификатора сотрудника при бронировании.
 * Аннотация объединяет несколько стандартных ограничений Bean Validation:</p>
 *
 * <ul>
 *     <li>значение не должно быть {@code null}</li>
 *     <li>идентификатор должен быть положительным числом</li>
 * </ul>
 *
 * <p>Применяется к полям и параметрам методов.</p>
 *
 * @see NotNull
 * @see Positive
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotNull(message = "{reservation.validation.employee.id.notNull}")
@Positive(message = "{reservation.validation.employee.id.positive}")
public @interface ReservationEmployeeIdValidation {

    String message() default "Invalid employee ID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
