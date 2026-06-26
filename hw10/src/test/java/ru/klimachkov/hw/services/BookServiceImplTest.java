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
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import(BookServiceImpl.class)
public class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private List<Author> authors;

    private List<Genre> genres;

    private List<Book> books;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(action -> {
            authors = TestDataGenerator.generateAuthors();
            authors.forEach(entityManager::persist);

            genres = TestDataGenerator.generateGenres();
            genres.forEach(entityManager::persist);

            books = TestDataGenerator.generateBooks(authors, genres);
            books.forEach(entityManager::persist);

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

    @DisplayName("Должен загружать книгу по id")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnBookById() {
        long targetId = books.get(0).getId();

        Optional<BookDto> actualBook = bookService.findById(targetId);

        assertThat(actualBook).isPresent();
        assertDoesNotThrowLazyInitializationException(actualBook.get());
    }

    @DisplayName("Должен загружать список всех книг")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnBooksList() {
        bookService.findAll().forEach(this::assertDoesNotThrowLazyInitializationException);
    }

    @DisplayName("Должен сохранять новую книгу")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveNewBook() {
        Set<Long> genresIds = genres.stream().limit(3).map(Genre::getId).collect(Collectors.toSet());
        BookDto insertedBook = bookService.insert("Inserted_Book", authors.get(0).getId(), genresIds);

        assertThat(insertedBook).isNotNull().matches(book -> book.getId() > 0);
        assertDoesNotThrowLazyInitializationException(insertedBook);
    }

    @DisplayName("Должен сохранять измененную книгу")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveUpdatedBook() {
        Set<Long> genresIds = genres.stream().skip(2).limit(3).map(Genre::getId).collect(Collectors.toSet());
        BookDto updatedBook = bookService.update(
                books.get(0).getId(), "Updated_Book", authors.get(authors.size() - 1).getId(), genresIds);

        assertThat(updatedBook).isNotNull();
        assertDoesNotThrowLazyInitializationException(updatedBook);
    }

    private void assertDoesNotThrowLazyInitializationException(@NonNull BookDto book) {
        assertNotNull(book.getAuthor());
        assertDoesNotThrow(() -> book.getAuthor().getFullName());

        assertThat(book.getGenres()).isNotEmpty();
        assertDoesNotThrow(() -> book.getGenres().size());
    }
}
