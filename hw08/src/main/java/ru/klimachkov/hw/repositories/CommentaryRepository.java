package ru.klimachkov.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.klimachkov.hw.models.Commentary;

import java.util.List;

public interface CommentaryRepository extends MongoRepository<Commentary, String> {
    List<Commentary> findByBookId(String bookId);

    void deleteByBookId(String bookId);
}
