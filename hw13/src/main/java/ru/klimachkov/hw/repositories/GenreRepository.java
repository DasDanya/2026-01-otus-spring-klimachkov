package ru.klimachkov.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends ListCrudRepository<Genre, Long> {
    List<Genre> findByIdIn(Set<Long> ids);
}
