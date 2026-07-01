package ru.klimachkov.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.BookGenre;
import ru.klimachkov.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class R2dbcBookRepository implements BookRepository {

    private static final String BOOKS_WITH_AUTHORS_SQL = """
            SELECT b.id, b.title, b.author_id, a.full_name
            FROM books b
            INNER JOIN authors a ON b.author_id = a.id
            """;

    private static final String BOOK_WITH_AUTHOR_SQL = BOOKS_WITH_AUTHORS_SQL + " WHERE b.id = $1";

    private final R2dbcEntityOperations entityOperations;

    private final GenreRepository genreRepository;

    @Override
    public Mono<BookDto> findById(long id) {
        return getBookWithAuthor(id)
            .flatMap(bookDto -> getBookGenresByBookId(id).collectList().flatMap(bookGenres -> {
                Set<Long> genresIds = bookGenres.stream().map(BookGenre::getGenreId).collect(Collectors.toSet());

                return genreRepository.findAllById(genresIds)
                    .collectList()
                    .map(genres -> {
                        List<GenreDto> genresDto = genres.stream().map(GenreDto::of).toList();
                        bookDto.setGenres(genresDto);
                        return bookDto;
                    });
            }));
    }

    @Override
    public Flux<BookDto> findAll() {
        return Flux.zip(getAllBooksWithAuthors().collectList(), getAllBookGenres().collectList()).flatMap(tuple -> {
            List<BookDto> books = tuple.getT1();
            List<BookGenre> bookGenres = tuple.getT2();
            Set<Long> genresIds = bookGenres.stream().map(BookGenre::getGenreId).collect(Collectors.toSet());

            return genreRepository.findAllById(genresIds)
                    .collectMap(Genre::getId)
                    .map(genresMap -> {
                        Map<Long, List<GenreDto>> genresByBook = bookGenres.stream()
                            .collect(Collectors.groupingBy(
                                    BookGenre::getBookId,
                                    Collectors.mapping(
                                        bookGenre -> GenreDto.of(genresMap.get(bookGenre.getGenreId())),
                                        Collectors.toList()))
                            );

                        books.forEach(book ->
                                book.setGenres(genresByBook.getOrDefault(book.getId(), new ArrayList<>())));

                        return books;
                    })
                    .flatMapMany(Flux::fromIterable);
        });
    }

    @Override
    public Mono<BookDto> save(BookDto bookDto) {
        return bookDto.getId() == 0L ? insert(bookDto) : update(bookDto);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return entityOperations.delete(Query.query(where("id").is(id)), Book.class).then();
    }

    private Mono<BookDto> getBookWithAuthor(long id) {
        return entityOperations.getDatabaseClient()
                .sql(BOOK_WITH_AUTHOR_SQL)
                .bind("$1", id)
                .fetch()
                .one()
                .map(this::mapBookDto);
    }

    private Flux<BookDto> getAllBooksWithAuthors() {
        return entityOperations.getDatabaseClient()
                .sql(BOOKS_WITH_AUTHORS_SQL)
                .fetch()
                .all()
                .map(this::mapBookDto);
    }

    private BookDto mapBookDto(Map<String, Object> row) {
        BookDto bookDto = new BookDto();
        bookDto.setId((long) row.get("id"));
        bookDto.setTitle((String) row.get("title"));
        bookDto.setAuthor(new AuthorDto((long) row.get("author_id"), (String) row.get("full_name")));
        return bookDto;
    }

    private Flux<BookGenre> getAllBookGenres() {
        return entityOperations.select(BookGenre.class).all();
    }

    private Flux<BookGenre> getBookGenresByBookId(long bookId) {
        return entityOperations.select(Query.query(where("book_id").is(bookId)), BookGenre.class);
    }

    private Mono<BookDto> insert(BookDto bookDto) {
        Book book = new Book(bookDto.getTitle(), bookDto.getAuthor().getId());
        return entityOperations.insert(book).flatMap(insertedBook -> {
            bookDto.setId(insertedBook.getId());
            return bookDto.getGenres().isEmpty()
                    ? Mono.just(bookDto)
                    : insertBookGenres(insertedBook.getId(), bookDto.getGenres()).thenReturn(bookDto);
        });
    }

    private Mono<BookDto> update(BookDto bookDto) {
        Book book = new Book(bookDto.getId(), bookDto.getTitle(), bookDto.getAuthor().getId());
        return entityOperations.update(book).flatMap(updatedBook ->
                deleteBookGenres(updatedBook.getId())
                        .then(bookDto.getGenres().isEmpty()
                                ? Mono.just(bookDto)
                                : insertBookGenres(updatedBook.getId(), bookDto.getGenres()).thenReturn(bookDto))
        );
    }

    private Mono<Void> insertBookGenres(long bookId, List<GenreDto> genres) {
        List<BookGenre> bookGenres = genres.stream().map(genre -> new BookGenre(bookId, genre.getId())).toList();
        return Flux.fromIterable(bookGenres).flatMap(entityOperations::insert).then();
    }

    private Mono<Void> deleteBookGenres(long bookId) {
        return entityOperations.delete(Query.query(where("book_id").is(bookId)), BookGenre.class).then();
    }
}
