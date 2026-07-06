package ru.klimachkov.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.klimachkov.hw.TestDataGenerator;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с комментариями книг")
@DataJpaTest
@Import(CommentaryServiceImpl.class)
public class CommentaryServiceImplTest {

    @Autowired
    private CommentaryServiceImpl commentaryService;

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
        commentaryService.findByBookId(targetBookId)
                .forEach(this::assertDoesNotThrowLazyInitializationException);
    }

    @DisplayName("Должен загружать комментарий по id")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnCommentaryById() {
        long targetId = commentaries.get(0).getId();

        Optional<Commentary> actualCommentary = commentaryService.findById(targetId);

        assertThat(actualCommentary).isPresent();
        assertDoesNotThrowLazyInitializationException(actualCommentary.get());
    }

    @DisplayName("Должен сохранять новый комментарий")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveNewCommentary() {
        Commentary insertedCommentary = commentaryService.insert("New_Commentary", books.get(0).getId());
        assertThat(insertedCommentary).isNotNull().matches(commentary -> commentary.getId() > 0);
        assertDoesNotThrowLazyInitializationException(insertedCommentary);
    }

    @DisplayName("Должен сохранять измененный комментарий")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveUpdatedCommentary() {
        Commentary updatedCommentary = commentaryService.update(
                commentaries.get(0).getId(), "Updated_Commentary", books.get(books.size() - 1).getId());

        assertThat(updatedCommentary).isNotNull();
        assertDoesNotThrowLazyInitializationException(updatedCommentary);
    }

    private void assertDoesNotThrowLazyInitializationException(@NonNull Commentary commentary) {
        assertNotNull(commentary.getBook());
        assertDoesNotThrow(() -> commentary.getBook().getTitle());

        assertNotNull(commentary.getBook().getAuthor());
        assertDoesNotThrow(() -> commentary.getBook().getAuthor().getFullName());

        assertThat(commentary.getBook().getGenres()).isNotEmpty();
        assertDoesNotThrow(() -> commentary.getBook().getGenres().size());
    }
}
