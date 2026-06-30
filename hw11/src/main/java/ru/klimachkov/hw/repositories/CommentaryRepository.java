package ru.klimachkov.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.CommentaryDto;

public interface CommentaryRepository {

    Mono<CommentaryDto> findById(long id);

    Flux<CommentaryDto> findByBookId(long bookId);

    Flux<CommentaryDto> findByBookIdWithoutFetch(long bookId);

    Mono<CommentaryDto> save(CommentaryDto commentaryDto);

    Mono<Void> deleteById(long id);
}
