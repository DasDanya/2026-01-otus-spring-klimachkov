package ru.klimachkov.workplaces.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Сущность бронирования рабочего места.
 *
 * <p>Представляет интервал времени, в течение которого конкретное рабочее место
 * забронировано за сотрудником.</p>
 *
 * <p>Бронирование связано с одним рабочим местом ({@link Workplace})
 * и одним сотрудником ({@link Employee}). При этом один сотрудник и одно
 * рабочее место могут иметь несколько бронирований в разное время.</p>
 *
 * <p>Ограничения по времени:
 * <ul>
 *     <li>Бронирования одного рабочего места не должны пересекаться по времени</li>
 *     <li>Бронирования одного сотрудника не должны пересекаться по времени</li>
 * </ul>
 * </p>
 *
 * @see Workplace
 * @see Employee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"workplace", "employee"})
@EqualsAndHashCode(exclude = {"workplace", "employee"})
@Entity
@Table(name = "reservations")
public class Reservation {

    /**
     * ID бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата и время начала бронирования
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Дата и время окончания бронирования
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Забронированное рабочее место
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workplace_id", nullable = false)
    private Workplace workplace;

    /**
     * Сотрудник, на которого забронировали рабочее место
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
