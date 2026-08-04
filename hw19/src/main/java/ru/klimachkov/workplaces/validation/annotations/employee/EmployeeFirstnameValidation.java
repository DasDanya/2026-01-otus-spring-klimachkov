package ru.klimachkov.workplaces.validation.annotations.employee;

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
 * Аннотация валидации имени сотрудника.
 *
 * <p>Используется для проверки корректности имени сотрудника.
 * Аннотация объединяет несколько стандартных ограничений Bean Validation:</p>
 *
 * <ul>
 *     <li>значение не должно быть пустым</li>
 *     <li>длина имени не должна превышать указанный лимит</li>
 *     <li>имя может содержать только буквы латиницы и кириллицы, а также дефис между частями имени</li>
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
@NotBlank(message = "{employee.validation.firstname.notBlank}")
@Size(max = 60, message = "{employee.validation.firstname.size}")
@Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+(-[A-Za-zА-Яа-яЁё]+)*$", message = "{employee.validation.firstname.pattern}")
public @interface EmployeeFirstnameValidation {

    String message() default "Invalid employee firstname";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
