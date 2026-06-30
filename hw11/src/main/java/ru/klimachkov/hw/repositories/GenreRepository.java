package ru.klimachkov.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.klimachkov.hw.models.Genre;

public interface GenreRepository extends ReactiveCrudRepository<Genre, Long> {
}
