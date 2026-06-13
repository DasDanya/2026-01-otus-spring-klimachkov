package ru.klimachkov.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.klimachkov.hw.converters.AuthorConverter;
import ru.klimachkov.hw.converters.BookConverter;
import ru.klimachkov.hw.converters.CommentaryConverter;
import ru.klimachkov.hw.converters.GenreConverter;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.TestDataGenerator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Сервис для работы с комментариями книг")
@DataJpaTest
@Import({CommentaryServiceImpl.class, CommentaryConverter.class,
        BookConverter.class, AuthorConverter.class, GenreConverter.class})
public class CommentaryServiceImplTest {

    @Autowired
    private CommentaryServiceImpl commentaryService;

    @Autowired
    private CommentaryConverter commentaryConverter;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private List<Book> books;

    private List<Commentary> commentaries;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(action -> {
            List<Author> authors = TestDataGenerator.generateAuthors();
            authors.forEach(entityManager::persist);

            List<Genre> genres = TestDataGenerator.generateGenres();
            genres.forEach(entityManager::persist);

            books = TestDataGenerator.generateBooks(authors, genres);
            books.forEach(entityManager::persist);

            commentaries = TestDataGenerator.generateCommentaries(books);
            commentaries.forEach(entityManager::persist);

            entityManager.flush();

            return null;
        });
    }

    @AfterEach
    public void cleanUp() {
        transactionTemplate.execute(action -> {
            entityManager.clear();
            return null;
        });
    }

    @DisplayName("Должен загружать список всех комментариев по id книги")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnCommentariesListByBookId() {
        long targetBookId = books.get(0).getId();
        List<Commentary> actualBookCommentaries = commentaryService.findByBookId(targetBookId);

        assertDoesNotThrow(() -> actualBookCommentaries.stream()
                .map(commentaryConverter::commentaryToString)
                .forEach(System.out::println));
    }

    @DisplayName("Должен загружать комментарий по id")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnCommentaryById() {
        long targetId = commentaries.get(0).getId();

        Optional<Commentary> actualCommentary = commentaryService.findById(targetId);

        assertThat(actualCommentary).isPresent();
        assertDoesNotThrow(() -> System.out.println(commentaryConverter.commentaryToString(actualCommentary.get())));
    }

    @DisplayName("Должен сохранять новый комментарий")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveNewCommentary() {
        Commentary insertedCommentary = commentaryService.insert("New_Commentary", books.get(0).getId());
        assertThat(insertedCommentary).isNotNull().matches(commentary -> commentary.getId() > 0);
        assertDoesNotThrow(() -> System.out.println(commentaryConverter.commentaryToString(insertedCommentary)));
    }

    @DisplayName("Должен сохранять измененный комментарий")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveUpdatedCommentary() {
        Commentary updatedCommentary = commentaryService.update(
                commentaries.get(0).getId(), "Updated_Commentary", books.get(books.size() - 1).getId());

        assertThat(updatedCommentary).isNotNull();
        assertDoesNotThrow(() -> System.out.println(commentaryConverter.commentaryToString(updatedCommentary)));
    }
}
