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
    public Optional<Commentary> findById(String id) {
        return commentaryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Commentary> findByBookId(String bookId) {
        return commentaryRepository.findByBookId(bookId);
    }

    @Override
    @Transactional
    public Commentary insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Override
    @Transactional
    public Commentary update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentaryRepository.deleteById(id);
    }

    private Commentary save(String id, String text, String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        return commentaryRepository.save(new Commentary(id, text, book));
    }
}
