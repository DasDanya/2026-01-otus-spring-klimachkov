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
 * Аннотация валидации фамилии сотрудника.
 *
 * <p>Используется для проверки корректности фамилии сотрудника.
 * Аннотация объединяет несколько стандартных ограничений Bean Validation:</p>
 *
 * <ul>
 *     <li>значение не должно быть пустым</li>
 *     <li>длина фамилии не должна превышать указанный лимит</li>
 *     <li>фамилия может содержать только буквы латиницы и кириллицы, а также дефис между частями фамилии</li>
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
@NotBlank(message = "{employee.validation.lastname.notBlank}")
@Size(max = 100, message = "{employee.validation.lastname.size}")
@Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+(-[A-Za-zА-Яа-яЁё]+)*$", message = "{employee.validation.lastname.pattern}")
public @interface EmployeeLastnameValidation {

    String message() default "Invalid employee lastname";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
