package ru.klimachkov.workplaces.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
 * Сущность сотрудника.
 *
 * <p>Каждый сотрудник связан ровно с одним пользователем системы через связь
 * {@link User}. Связь является обязательной и уникальной.</p>
 *
 * <p>Сотрудник может быть закреплён за рабочим местом.
 * Связь с рабочим местом является односторонней и хранится в сущности {@link Workplace}</p>
 *
 * <p>Сотрудник может иметь список бронирований рабочих мест.
 * При удалении сотрудника связанные бронирования также удаляются</p>
 *
 * @see User
 * @see Workplace
 * @see Reservation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "reservations"})
@EqualsAndHashCode(exclude = {"user", "reservations"})
@Entity
@Table(name = "employees")
public class Employee {

    /**
     * ID сотрудника
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя сотрудника
     */
    @Column(name = "firstname", nullable = false, length = 60)
    private String firstname;

    /**
     * Фамилия сотрудника
     */
    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    /**
     * Отчество сотрудника.
     *
     * <p>Может отсутствовать</p>
     */
    @Column(name = "middlename", length = 60)
    private String middlename;

    /**
     * Пользователь системы, связанный с сотрудником
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Список бронирований сотрудника
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Добавляет бронирование сотруднику.
     *
     * <p>Метод поддерживает двунаправленную связь между сотрудником и бронированием:
     * добавляет бронирование в список текущего сотрудника и устанавливает ссылку
     * на сотрудника в объекте {@link Reservation}.</p>
     *
     * @param reservation бронирование, которое необходимо добавить
     */
    public void addReservation(@NonNull Reservation reservation) {
        reservations.add(reservation);
        reservation.setEmployee(this);
    }
}
