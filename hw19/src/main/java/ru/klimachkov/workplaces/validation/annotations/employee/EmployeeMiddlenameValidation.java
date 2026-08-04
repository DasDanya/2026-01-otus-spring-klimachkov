package ru.klimachkov.workplaces.validation.annotations.employee;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация валидации отчества сотрудника.
 *
 * <p>Используется для проверки корректности отчества сотрудника.
 * Аннотация объединяет несколько стандартных ограничений Bean Validation:</p>
 *
 * <ul>
 *     <li>длина отчества не должна превышать указанный лимит</li>
 *     <li>отчество может содержать только буквы латиницы и кириллицы, а также дефис между частями отчества</li>
 * </ul>
 *
 * <p>Значение является необязательным, но при наличии должно соответствовать указанным правилам валидации.</p>
 *
 * <p>Применяется к полям и параметрам методов.</p>
 *
 * @see Size
 * @see Pattern
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Size(max = 60, message = "{employee.validation.middlename.size}")
@Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+(-[A-Za-zА-Яа-яЁё]+)*$", message = "{employee.validation.middlename.pattern}")
public @interface EmployeeMiddlenameValidation {

    String message() default "Invalid employee middlename";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
