package ru.klimachkov.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.klimachkov.hw.models.Author;

public interface AuthorRepository extends ReactiveCrudRepository<Author, Long> {
}
