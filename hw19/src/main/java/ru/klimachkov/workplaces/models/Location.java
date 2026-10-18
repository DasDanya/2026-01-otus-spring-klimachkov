package ru.klimachkov.workplaces.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность локации.
 *
 * <p>Каждая локация имеет уникальное название и может содержать несколько рабочих мест.</p>
 *
 * <p>При удалении локации связанные рабочие места также удаляются</p>
 *
 * @see Workplace
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    /**
     * ID локации
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название локации
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Список рабочих мест, расположенных в данной локации
     */
    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Workplace> workplaces = new ArrayList<>();

    /**
     * Добавляет рабочее место в текущую локацию.
     *
     * <p>Метод поддерживает двунаправленную связь между локацией и рабочим местом:
     * добавляет рабочее место в список текущей локации и устанавливает текущую локацию
     * для объекта {@link Workplace}.</p>
     *
     * @param workplace рабочее место, которое необходимо добавить
     */
    public void addWorkplace(@NonNull Workplace workplace) {
        workplaces.add(workplace);
        workplace.setLocation(this);
    }
}
