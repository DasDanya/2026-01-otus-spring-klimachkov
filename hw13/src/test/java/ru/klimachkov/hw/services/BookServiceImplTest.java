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
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.klimachkov.hw.TestDataCreator;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.security.acl.AclConfig;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import({BookServiceImpl.class, AclConfig.class})
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
            authors = TestDataCreator.createAuthors();
            authors.forEach(entityManager::persist);

            genres = TestDataCreator.createGenres();
            genres.forEach(entityManager::persist);

            books = TestDataCreator.createBooks(authors, genres);
            books.forEach(entityManager::persist);

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

    @DisplayName("Должен загрузить книгу по id")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnBookById() {
        long targetId = books.get(0).getId();

        Optional<Book> actualBook = bookService.findById(targetId);

        assertThat(actualBook).isPresent();
        assertDoesNotThrowLazyInitializationException(actualBook.get());
    }

    @DisplayName("Должен загрузить список всех книг")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnBooksList() {
        bookService.findAll().forEach(this::assertDoesNotThrowLazyInitializationException);
    }

    @DisplayName("Администратор должен сохранить новую книгу")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveNewBookWhenUserIsAdmin() {
        Set<Long> genresIds = genres.stream().limit(3).map(Genre::getId).collect(Collectors.toSet());
        Book insertedBook = bookService.insert("Inserted_Book", authors.get(0).getId(), genresIds);

        assertThat(insertedBook).isNotNull().matches(book -> book.getId() > 0);
        assertDoesNotThrowLazyInitializationException(insertedBook);
    }

    @DisplayName("Обычный пользователь не должен сохранить новую книгу")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotSaveNewBookWhenUserIsNotAdmin() {
        Set<Long> genresIds = genres.stream().limit(3).map(Genre::getId).collect(Collectors.toSet());

        assertThatThrownBy(() -> bookService.insert("Inserted_Book", authors.get(0).getId(), genresIds))
                .isInstanceOf(AuthorizationDeniedException.class);
    }

    @DisplayName("Администратор должен сохранить измененную книгу")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveUpdatedBookWhenUserIsAdmin() {
        Set<Long> genresIds = genres.stream().skip(2).limit(3).map(Genre::getId).collect(Collectors.toSet());
        Book updatedBook = bookService.update(
                books.get(0).getId(), "Updated_Book", authors.get(authors.size() - 1).getId(), genresIds);

        assertThat(updatedBook).isNotNull();
        assertDoesNotThrowLazyInitializationException(updatedBook);
    }

    @DisplayName("Обычный пользователь не должен сохранить измененную книгу")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotSaveUpdatedBookWhenUserIsNotAdmin() {
        Set<Long> genresIds = genres.stream().skip(2).limit(3).map(Genre::getId).collect(Collectors.toSet());

        assertThatThrownBy(() -> bookService.update(
                books.get(0).getId(), "Updated_Book", authors.get(authors.size() - 1).getId(), genresIds)
        ).isInstanceOf(AuthorizationDeniedException.class);
    }

    @DisplayName("Администратор должен удалить книгу")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldDeleteBookWhenUserIsAdmin() {
        long bookId = books.get(0).getId();
        assertThatCode(() -> bookService.deleteById(bookId)).doesNotThrowAnyException();
    }

    @DisplayName("Обычный пользователь не должен удалить книгу")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotDeleteBookWhenUserIsNotAdmin() {
        long bookId = books.get(0).getId();
        assertThatThrownBy(() -> bookService.deleteById(bookId)).isInstanceOf(AuthorizationDeniedException.class);
    }

    private void assertDoesNotThrowLazyInitializationException(@NonNull Book book) {
        assertNotNull(book.getAuthor());
        assertDoesNotThrow(() -> book.getAuthor().getFullName());

        assertThat(book.getGenres()).isNotEmpty();
        assertDoesNotThrow(() -> book.getGenres().size());
    }
}
