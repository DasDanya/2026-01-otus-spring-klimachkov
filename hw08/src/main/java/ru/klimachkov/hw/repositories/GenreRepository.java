package ru.klimachkov.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends MongoRepository<Genre, String> {
    List<Genre> findByIdIn(Set<String> ids);
}
