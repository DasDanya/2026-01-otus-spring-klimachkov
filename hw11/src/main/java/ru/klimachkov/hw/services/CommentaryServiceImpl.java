package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.CommentaryRepository;

@Service
@RequiredArgsConstructor
public class CommentaryServiceImpl implements CommentaryService {

    private final BookRepository bookRepository;

    private final CommentaryRepository commentaryRepository;

    @Override
    public Mono<CommentaryDto> findById(long id) {
        return commentaryRepository.findById(id);
    }

    @Override
    public Flux<CommentaryDto> findByBookId(long bookId) {
        return commentaryRepository.findByBookId(bookId);
    }

    @Override
    public Flux<CommentaryDto> findByBookIdWithoutFetch(long bookId) {
        return commentaryRepository.findByBookIdWithoutFetch(bookId);
    }

    @Override
    public Mono<CommentaryDto> insert(String text, long bookId) {
        return save(0L, text, bookId);
    }

    @Override
    public Mono<CommentaryDto> update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return commentaryRepository.deleteById(id);
    }

    private Mono<CommentaryDto> save(long id, String text, long bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %d not found".formatted(bookId))))
                .flatMap(book -> commentaryRepository.save(new CommentaryDto(id, text, book)));
    }
}
