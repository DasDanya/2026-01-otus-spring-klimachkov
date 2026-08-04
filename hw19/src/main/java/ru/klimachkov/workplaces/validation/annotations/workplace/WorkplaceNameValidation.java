package ru.klimachkov.workplaces.validation.annotations.workplace;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация валидации названия рабочего места.
 *
 * <p>Используется для проверки корректности названия рабочего места.
 * Аннотация объединяет несколько стандартных ограничений Bean Validation:</p>
 *
 * <ul>
 *     <li>значение не должно быть пустым</li>
 *     <li>длина названия не должна превышать указанный лимит</li>
 *     <li>название может содержать только латинские буквы и цифры</li>
 * </ul>
 *
 * <p>Применяется к полям и параметрам методов.</p>
 *
 * @see NotBlank
 * @see Size
 * @see Pattern
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "{workplace.validation.name.notBlank}")
@Size(max = 3, message = "{workplace.validation.name.size}")
@Pattern(regexp = "^[A-Za-z0-9]+$", message = "{workplace.validation.name.pattern}")
public @interface WorkplaceNameValidation {

    String message() default "Invalid workplace name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
