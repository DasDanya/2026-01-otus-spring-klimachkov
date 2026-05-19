package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.CommentaryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentaryServiceImpl implements CommentaryService {

    private final CommentaryRepository commentaryRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Commentary> findById(long id) {
        return commentaryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Commentary> findAllByBookId(long bookId) {
        return commentaryRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional
    public Commentary insert(String text, long bookId) {
        return save(0L, text, bookId);
    }

    @Override
    @Transactional
    public Commentary update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    private Commentary save(long id, String text, long bookId) {
        Book book = bookRepository.findById(bookId).
                orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        return commentaryRepository.save(new Commentary(id, text, book));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentaryRepository.deleteById(id);
    }
}
