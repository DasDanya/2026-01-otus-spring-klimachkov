package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.repositories.AuthorRepository;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @Override
    public Mono<BookDto> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Mono<BookDto> insert(String title, long authorId, Set<Long> genresIds) {
        return save(0L, title, authorId, genresIds);
    }

    @Override
    public Mono<BookDto> update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    private Mono<BookDto> save(long id, String title, long authorId, Set<Long> genresIds) {
        if (genresIds == null || genresIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null"));
        }

        return Mono.zip(getAuthor(authorId), getGenres(genresIds))
                .flatMap(tuple -> bookRepository.save(new BookDto(id, title, tuple.getT1(), tuple.getT2())));
    }

    private Mono<AuthorDto> getAuthor(long authorId) {
        return authorRepository.findById(authorId)
                .map(AuthorDto::of)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %s not found".formatted(authorId))));
    }

    private Mono<List<GenreDto>> getGenres(Set<Long> genresIds) {
        return genreRepository.findAllById(genresIds)
                .collectList()
                .flatMap(genres -> {
                    if (CollectionUtils.isEmpty(genres) || genresIds.size() != genres.size()) {
                        return Mono.error(new EntityNotFoundException(
                                "One or all genres with ids %s not found".formatted(genresIds))
                        );
                    }

                    List<GenreDto> genresDto = genres.stream().map(GenreDto::of).toList();
                    return Mono.just(genresDto);
                });
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return bookRepository.deleteById(id);
    }
}
