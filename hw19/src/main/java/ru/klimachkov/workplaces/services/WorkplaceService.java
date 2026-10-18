package ru.klimachkov.workplaces.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceCreationDto;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceFilterModel;
import ru.klimachkov.workplaces.dto.workplace.WorkplaceResponseDto;
import ru.klimachkov.workplaces.models.Workplace;

/**
 * Сервис для работы с рабочими местами.
 *
 * <p>Предоставляет операции для получения, создания, изменения
 * и удаления рабочих мест, а также управления их типом и привязкой
 * к сотрудникам.</p>
 */
public interface WorkplaceService {

    @NonNull
    Workplace findById(Long id);

    @NonNull
    WorkplaceResponseDto findByIdWithRelations(Long id);

    PagedModel<WorkplaceResponseDto> findAll(WorkplaceFilterModel workplaceFilterModel, Pageable pageable);

    WorkplaceResponseDto create(WorkplaceCreationDto workplaceCreationDto);

    WorkplaceResponseDto rename(Long id, String name);

    WorkplaceResponseDto convertToHotDesk(Long id);

    WorkplaceResponseDto convertToIndividual(Long id, Long employeeId);

    WorkplaceResponseDto replaceEmployee(Long id, Long employeeId);

    void delete(Long id);

    void deleteByLocation(Long locationId);

    void deleteByEmployee(Long employeeId);
}
