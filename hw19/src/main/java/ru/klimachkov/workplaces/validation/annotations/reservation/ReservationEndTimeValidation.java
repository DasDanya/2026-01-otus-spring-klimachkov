package ru.klimachkov.workplaces.validation.annotations.reservation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация валидации времени окончания бронирования.
 *
 * <p>Используется для проверки корректности даты и времени окончания бронирования.
 * Аннотация объединяет несколько стандартных ограничений Bean Validation:</p>
 *
 * <ul>
 *     <li>значение не должно быть {@code null}</li>
 *     <li>дата и время окончания бронирования должны быть в будущем</li>
 * </ul>
 *
 * <p>Применяется к полям и параметрам методов.</p>
 *
 * @see NotNull
 * @see Future
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotNull(message = "{reservation.validation.endTime.notNull}")
@Future(message = "{reservation.validation.endTime.future}")
public @interface ReservationEndTimeValidation {

    String message() default "Invalid reservation end time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
