package ru.klimachkov.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.klimachkov.hw.models.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
