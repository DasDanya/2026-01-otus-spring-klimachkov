package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import reactor.test.StepVerifier;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с комментариями")
@DataR2dbcTest
@Import({R2dbcCommentaryRepository.class, R2dbcBookRepository.class, TestDataFactory.class})
public class CommentaryRepositoryTest {

    @Autowired
    private R2dbcEntityOperations entityOperations;

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @AfterEach
    void cleanUp() {
        entityOperations.delete(Commentary.class).all()
                .then(entityOperations.delete(Book.class).all())
                .then(entityOperations.delete(Genre.class).all())
                .then(entityOperations.delete(Author.class).all())
                .block();
    }

    @DisplayName("Должен загрузить комментарий по id")
    @Test
    void shouldReturnCorrectCommentaryById() {
        CommentaryDto expectedCommentary = testDataFactory.createCommentary();

        StepVerifier.create(commentaryRepository.findById(expectedCommentary.getId()))
                .assertNext(actualCommentary ->
                        assertThat(actualCommentary).usingRecursiveComparison().isEqualTo(expectedCommentary))
                .verifyComplete();
    }

    @DisplayName("Должен загрузить список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentariesListByBookId() {
        BookDto book = testDataFactory.createBook(1);
        List<CommentaryDto> expectedCommentaries = IntStream.range(1, 4)
                .mapToObj(i -> testDataFactory.createCommentary(book, i))
                .toList();

        StepVerifier.create(commentaryRepository.findByBookId(book.getId()).collectList())
                .assertNext(actualCommentaries ->
                        assertThat(actualCommentaries).usingRecursiveComparison().isEqualTo(expectedCommentaries))
                .verifyComplete();
    }

    @DisplayName("Должен загрузить список всех комментариев по id книги без fetch")
    @Test
    void shouldReturnCorrectCommentariesListWithoutFetchByBookId() {
        BookDto book = testDataFactory.createBook();
        List<CommentaryDto> expectedCommentaries = IntStream.range(1, 4)
                .mapToObj(i -> testDataFactory.createCommentary(book, i))
                .toList();

        StepVerifier.create(commentaryRepository.findByBookIdWithoutFetch(book.getId()).collectList())
                .assertNext(actualCommentaries -> {
                    assertThat(actualCommentaries)
                            .extracting(CommentaryDto::getBook)
                            .containsOnlyNulls();

                    assertThat(actualCommentaries)
                            .usingRecursiveComparison()
                            .ignoringFields("book")
                            .isEqualTo(expectedCommentaries);
                })
                .verifyComplete();
    }

    @DisplayName("Должен сохранить новый комментарий")
    @Test
    void shouldSaveNewCommentary() {
        CommentaryDto newCommentary = new CommentaryDto();
        newCommentary.setText("NewCommentary");
        newCommentary.setBook(testDataFactory.createBook());

        StepVerifier.create(
            commentaryRepository.save(newCommentary)
                .doOnNext(savedCommentary -> {
                    assertThat(savedCommentary.getId()).isGreaterThan(0);
                    assertThat(savedCommentary)
                            .usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(newCommentary);
                })
                .flatMap(savedCommentary -> commentaryRepository.findById(savedCommentary.getId())
                        .doOnNext(foundCommentary ->
                                assertThat(foundCommentary).usingRecursiveComparison().isEqualTo(savedCommentary))
                )
        )
        .expectNextCount(1)
        .verifyComplete();
    }

    @DisplayName("Должен сохранить измененный комментарий")
    @Test
    void shouldSaveUpdatedCommentary() {
        CommentaryDto commentaryForUpdate = testDataFactory.createCommentary();
        commentaryForUpdate.setText("UpdatedCommentary");
        commentaryForUpdate.setBook(testDataFactory.createBook(999));

        StepVerifier.create(
            commentaryRepository.save(commentaryForUpdate)
                .doOnNext(savedCommentary ->
                        assertThat(savedCommentary).usingRecursiveComparison().isEqualTo(commentaryForUpdate))
                .flatMap(savedCommentary ->
                        commentaryRepository.findById(savedCommentary.getId())
                            .doOnNext(foundCommentary ->
                                    assertThat(foundCommentary).usingRecursiveComparison().isEqualTo(savedCommentary))
                )
        )
        .expectNextCount(1)
        .verifyComplete();
    }

    @DisplayName("Должен удалить комментарий по id")
    @Test
    void shouldDeleteCommentary() {
        CommentaryDto commentaryForDelete = testDataFactory.createCommentary();

        StepVerifier.create(commentaryRepository.deleteById(commentaryForDelete.getId())).verifyComplete();
        StepVerifier.create(commentaryRepository.findById(commentaryForDelete.getId())).verifyComplete();
    }

}
