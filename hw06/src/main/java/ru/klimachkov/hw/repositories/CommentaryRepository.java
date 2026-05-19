package ru.klimachkov.hw.repositories;

import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.Optional;

public interface CommentaryRepository {
    Optional<Commentary> findById(long id);

    List<Commentary> findAllByBookId(long bookId);

    Commentary save(Commentary commentary);

    void deleteById(long id);
}
