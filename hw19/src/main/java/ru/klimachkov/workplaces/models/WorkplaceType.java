package ru.klimachkov.workplaces.models;

/**
 * Типы рабочих мест.
 *
 * <p>Определяет способ использования рабочего места и правила его
 * закрепления и бронирования.</p>
 *
 * <ul>
 *     <li>{@link #INDIVIDUAL} — индивидуальное рабочее место,
 *     закрепленное за конкретным сотрудником</li>
 *     <li>{@link #HOT_DESK} — общее рабочее место,
 *     доступное для бронирования различными сотрудниками</li>
 * </ul>
 *
 * @see Workplace
 */
public enum WorkplaceType {
    INDIVIDUAL,
    HOT_DESK
}
