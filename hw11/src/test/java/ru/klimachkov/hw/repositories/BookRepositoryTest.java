package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами")
@DataR2dbcTest
@Import({R2dbcBookRepository.class, TestDataFactory.class})
public class BookRepositoryTest {

    @Autowired
    private R2dbcEntityOperations entityOperations;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @AfterEach
    void cleanUp() {
        entityOperations.delete(Book.class).all()
                .then(entityOperations.delete(Genre.class).all())
                .then(entityOperations.delete(Author.class).all())
                .block();
    }

    @DisplayName("Должен загрузить книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        BookDto expectedBook = testDataFactory.createBook();

        StepVerifier.create(bookRepository.findById(expectedBook.getId()))
                .assertNext(actualBook -> assertThat(actualBook).usingRecursiveComparison().isEqualTo(expectedBook))
                .verifyComplete();
    }

    @DisplayName("Должен загрузить список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<BookDto> expectedBooks = IntStream.range(1, 4).mapToObj(i -> testDataFactory.createBook(i)).toList();

        StepVerifier.create(bookRepository.findAll().collectList())
                .assertNext(actualBooks -> assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks))
                .verifyComplete();
    }

    @DisplayName("Должен сохранить новую книгу")
    @Test
    void shouldSaveNewBook() {
        BookDto newBook = new BookDto();
        newBook.setTitle("NewBook");
        newBook.setAuthor(testDataFactory.createAuthor(1));
        newBook.setGenres(List.of(testDataFactory.createGenre(1),testDataFactory.createGenre(2)));

        StepVerifier.create(
            bookRepository.save(newBook)
            .doOnNext(savedBook -> {
                assertThat(savedBook.getId()).isGreaterThan(0);
                assertThat(savedBook)
                        .usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(newBook);
            })
            .flatMap(savedBook ->
                bookRepository.findById(savedBook.getId())
                        .doOnNext(foundBook -> assertThat(foundBook).usingRecursiveComparison().isEqualTo(savedBook))
            )
        )
        .expectNextCount(1)
        .verifyComplete();
    }

    @DisplayName("Должен сохранить измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        BookDto bookForUpdate = testDataFactory.createBook();
        bookForUpdate.setTitle("UpdatedBook");
        bookForUpdate.setAuthor(testDataFactory.createAuthor(999));
        bookForUpdate.setGenres(Collections.singletonList(testDataFactory.createGenre(999)));

        StepVerifier.create(
            bookRepository.save(bookForUpdate)
            .doOnNext(savedBook -> assertThat(savedBook).usingRecursiveComparison().isEqualTo(bookForUpdate))
            .flatMap(savedBook ->
                bookRepository.findById(savedBook.getId())
                        .doOnNext(foundBook -> assertThat(foundBook).usingRecursiveComparison().isEqualTo(savedBook))
            )
        )
        .expectNextCount(1)
        .verifyComplete();
    }

    @DisplayName("Должен удалить книгу по id")
    @Test
    void shouldDeleteBook() {
        BookDto bookForDelete = testDataFactory.createBook();

        StepVerifier.create(bookRepository.deleteById(bookForDelete.getId())).verifyComplete();
        StepVerifier.create(bookRepository.findById(bookForDelete.getId())).verifyComplete();
    }
}
