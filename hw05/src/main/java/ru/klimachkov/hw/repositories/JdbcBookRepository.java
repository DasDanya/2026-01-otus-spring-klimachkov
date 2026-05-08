package ru.klimachkov.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private static final ResultSetExtractor<Book> BOOK_RESULT_SET_EXTRACTOR = rs -> {
        Book book = null;
        while (rs.next()) {
            if (book == null) {
                book = new Book();
                book.setId(rs.getLong("book_id"));
                book.setTitle(rs.getString("book_title"));
                book.setAuthor(new Author(rs.getLong("author_id"), rs.getString("full_name")));
                book.setGenres(new ArrayList<>());
            }

            book.getGenres().add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
        }

        return book;
    };

    private static final RowMapper<Book> BOOK_AUTHOR_ROW_MAPPER = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(new Author(rs.getLong("author_id"), rs.getString("full_name")));
        return book;
    };

    private static final RowMapper<BookGenreRelation> BOOK_GENRE_RELATION_ROW_MAPPER = (rs, rowNum) ->
            new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));

    private final NamedParameterJdbcOperations jdbc;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        Book book = jdbc.query(
                "SELECT book_id, title AS book_title, author_id, full_name, genre_id, name AS genre_name " +
                        "FROM books_genres " +
                        "INNER JOIN genres g ON genre_id = g.id " +
                        "INNER JOIN books b ON book_id = b.id " +
                        "INNER JOIN authors a ON author_id = a.id " +
                        "WHERE book_id = :book_id",
                Collections.singletonMap("book_id", id), BOOK_RESULT_SET_EXTRACTOR);

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        List<Genre> genres = genreRepository.findAll();
        List<Book> books = getAllBooksWithoutGenres();
        List<BookGenreRelation> relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);

        return books;
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbc.query(
                "SELECT b.id, b.title, b.author_id, a.full_name FROM books b " +
                        "INNER JOIN authors a ON b.author_id = a.id",
                BOOK_AUTHOR_ROW_MAPPER);
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbc.query("SELECT book_id, genre_id FROM books_genres", BOOK_GENRE_RELATION_ROW_MAPPER);
    }

    private void mergeBooksInfo(
            List<Book> booksWithoutGenres,
            List<Genre> genres,
            List<BookGenreRelation> relations
    ) {
        Map<Long, Genre> genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));
        Map<Long, List<Genre>> bookGenres = relations.stream()
                .filter(relation -> genresMap.containsKey(relation.genreId))
                .collect(Collectors.groupingBy(
                                BookGenreRelation::bookId,
                                Collectors.mapping(relation -> genresMap.get(relation.genreId), Collectors.toList()))
                );

        booksWithoutGenres.forEach(book -> book.setGenres(bookGenres.getOrDefault(book.getId(), new ArrayList<>())));
    }

    @Override
    public Book save(Book book) {
        return book.getId() == 0L ? insert(book) : update(book);
    }

    private Book insert(Book book) {
        MapSqlParameterSource insertParams = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        KeyHolder idHolder = new GeneratedKeyHolder();

        jdbc.update("INSERT INTO books (title, author_id) VALUES (:title, :author_id)", insertParams, idHolder);
        book.setId(Objects.requireNonNull(idHolder.getKey()).longValue());

        batchInsertGenresRelationsFor(book);

        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource updateParams = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        int updatedBooks = jdbc.update(
                "UPDATE books SET title = :title, author_id = :author_id WHERE id = :id", updateParams);

        if (updatedBooks == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        if (!book.getGenres().isEmpty()) {
            MapSqlParameterSource[] insertBooksGenresParams = book.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource("book_id", book.getId())
                            .addValue("genre_id", genre.getId())
                    ).toArray(MapSqlParameterSource[]::new);

            jdbc.batchUpdate("INSERT INTO books_genres (book_id, genre_id) VALUES (:book_id, :genre_id)",
                    insertBooksGenresParams);
        }
    }

    private void removeGenresRelationsFor(Book book) {
        jdbc.update("DELETE FROM books_genres WHERE book_id = :book_id",
                Collections.singletonMap("book_id", book.getId()));
    }

    @Override
    public void deleteById(long id) {
        jdbc.update("DELETE FROM books WHERE id = :id", Collections.singletonMap("id", id));
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
