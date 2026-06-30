package ru.klimachkov.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.CommentaryDto;

public interface CommentaryService {

    Mono<CommentaryDto> findById(long id);

    Flux<CommentaryDto> findByBookId(long bookId);

    Flux<CommentaryDto> findByBookIdWithoutFetch(long bookId);

    Mono<CommentaryDto> insert(String text, long bookId);

    Mono<CommentaryDto> update(long id, String text, long bookId);

    Mono<Void> deleteById(long id);
}
