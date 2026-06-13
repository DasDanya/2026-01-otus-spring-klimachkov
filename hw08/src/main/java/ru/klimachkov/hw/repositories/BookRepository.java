package ru.klimachkov.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.klimachkov.hw.models.Book;

public interface BookRepository extends MongoRepository<Book, String> {
}
