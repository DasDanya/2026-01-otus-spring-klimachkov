package ru.klimachkov.hw.services;

import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.Optional;

public interface CommentaryService {
    Optional<Commentary> findById(long id);

    List<Commentary> findAllByBookId(long bookId);

    Commentary insert(String text, long bookId);

    Commentary update(long id, String text, long bookId);

    void deleteById(long id);
}
