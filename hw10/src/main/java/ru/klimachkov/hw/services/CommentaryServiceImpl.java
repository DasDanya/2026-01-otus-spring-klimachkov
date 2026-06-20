package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klimachkov.hw.dto.CommentaryDto;
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

    private final BookRepository bookRepository;

    private final CommentaryRepository commentaryRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentaryDto> findById(long id) {
        return commentaryRepository.findById(id).map(CommentaryDto::of);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentaryDto> findByBookId(long bookId) {
        return commentaryRepository.findByBookId(bookId).stream().map(CommentaryDto::of).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentaryDto> findByBookIdWithoutFetch(long bookId) {
        return commentaryRepository.findByBookIdWithoutFetch(bookId).stream()
                .map(commentary -> new CommentaryDto(commentary.getId(), commentary.getText()))
                .toList();
    }

    @Override
    @Transactional
    public CommentaryDto insert(String text, long bookId) {
        return save(0L, text, bookId);
    }

    @Override
    @Transactional
    public CommentaryDto update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentaryRepository.deleteById(id);
    }

    private CommentaryDto save(long id, String text, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        Commentary savedCommentary = commentaryRepository.save(new Commentary(id, text, book));
        return CommentaryDto.of(savedCommentary);
    }
}
