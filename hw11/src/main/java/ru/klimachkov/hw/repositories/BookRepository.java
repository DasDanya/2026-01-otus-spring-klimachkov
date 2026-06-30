package ru.klimachkov.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.BookDto;

public interface BookRepository {

   Mono<BookDto> findById(long id);

   Flux<BookDto> findAll();

   Mono<BookDto> save(BookDto bookDto);

   Mono<Void> deleteById(long id);
}
