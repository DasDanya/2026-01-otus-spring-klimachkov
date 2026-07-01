package ru.klimachkov.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.BookDto;

import java.util.Set;

public interface BookService {

    Mono<BookDto> findById(long id);

    Flux<BookDto> findAll();

    Mono<BookDto> insert(String title, long authorId, Set<Long> genresIds);

    Mono<BookDto> update(long id, String title, long authorId, Set<Long> genresIds);

    Mono<Void> deleteById(long id);
}
