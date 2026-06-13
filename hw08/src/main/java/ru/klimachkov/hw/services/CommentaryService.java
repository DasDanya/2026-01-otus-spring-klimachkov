package ru.klimachkov.hw.services;

import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.Optional;

public interface CommentaryService {
    Optional<Commentary> findById(String id);

    List<Commentary> findByBookId(String bookId);

    Commentary insert(String text, String bookId);

    Commentary update(String id, String text, String bookId);

    void deleteById(String id);
}
