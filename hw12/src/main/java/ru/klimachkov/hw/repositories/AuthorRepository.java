package ru.klimachkov.hw.repositories;

import org.springframework.data.repository.ListCrudRepository;
import ru.klimachkov.hw.models.Author;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
}
