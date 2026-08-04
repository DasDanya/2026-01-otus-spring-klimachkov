package ru.klimachkov.workplaces.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность рабочего места.
 *
 * <p>Представляет рабочее место в рамках определённой локации ({@link Location}).
 * Рабочее место характеризуется названием, типом ({@link WorkplaceType}) и вместимостью.</p>
 *
 * <p>Рабочее место может быть закреплено за сотрудником ({@link Employee})
 * (такое рабочее место является индивидуальным), либо использоваться как общее (hot-desk).</p>
 *
 * <p>Каждое рабочее место принадлежит одной локации.
 * Название рабочего места уникально в пределах локации.</p>
 *
 * <p>Рабочее место может иметь список бронирований.
 * При удалении рабочего места связанные бронирования также удаляются.</p>
 *
 * @see WorkplaceType
 * @see Location
 * @see Employee
 * @see Reservation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"location", "employee", "reservations"})
@EqualsAndHashCode(exclude = {"location", "employee", "reservations"})
@Entity
@Table(name = "workplaces", uniqueConstraints =
    @UniqueConstraint(name = "uq_workplaces_location_name", columnNames = {"location_id", "name"}))
public class Workplace {

    /**
     * ID рабочего места
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название рабочего места (уникально в пределах локации)
     */
    @Column(name = "name", nullable = false, length = 3)
    private String name;

    /**
     * Тип рабочего места
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WorkplaceType type;

    /**
     * Вместимость рабочего места
     */
    @Column(name = "capacity", nullable = false)
    private int capacity;

    /**
     * Локация, к которой относится рабочее место
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /**
     * Сотрудник, привязанный к рабочему месту
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;

    /**
     * Список бронирований данного рабочего места
     */
    @OneToMany(mappedBy = "workplace", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Добавляет бронирование к рабочему месту.
     *
     * <p>Метод поддерживает двунаправленную связь между рабочим местом и бронированием:
     * добавляет бронирование в список текущего рабочего места и устанавливает ссылку
     * на рабочее место в {@link Reservation}.</p>
     *
     * @param reservation бронирование, которое необходимо добавить
     */
    public void addReservation(@NonNull Reservation reservation) {
        reservations.add(reservation);
        reservation.setWorkplace(this);
    }
}
