package ru.klimachkov.hw.services;

import ru.klimachkov.hw.dto.CommentaryDto;

import java.util.List;
import java.util.Optional;

public interface CommentaryService {
    Optional<CommentaryDto> findById(long id);

    List<CommentaryDto> findByBookId(long bookId);

    List<CommentaryDto> findByBookIdWithoutFetch(long bookId);

    CommentaryDto insert(String text, long bookId);

    CommentaryDto update(long id, String text, long bookId);

    void deleteById(long id);
}
