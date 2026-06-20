package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.klimachkov.hw.TestDataGenerator;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Author> authors;

    private List<Genre> genres;

    private List<Book> books;

    @BeforeEach
    void setUp() {
        authors = TestDataGenerator.generateAuthors();
        authors.forEach(entityManager::persist);

        genres = TestDataGenerator.generateGenres();
        genres.forEach(entityManager::persist);

        books = TestDataGenerator.generateBooks(authors, genres);
        books.forEach(entityManager::persist);

        entityManager.flush();
    }

    @DisplayName("Должен загрузить книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        long targetId = books.get(0).getId();

        Optional<Book> optionalActualBook = bookRepository.findById(targetId);
        Book expectedBook = entityManager.find(Book.class, targetId);

        assertThat(optionalActualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("Должен загрузить список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookRepository.findAll();
        assertThat(actualBooks).containsExactlyElementsOf(books);
    }

    @DisplayName("Должен сохранить новую книгу")
    @Test
    void shouldSaveNewBook() {
        Book expectedBook = new Book();
        expectedBook.setTitle("New_Book");
        expectedBook.setAuthor(authors.get(0));
        expectedBook.setGenres(genres.subList(0, 2));

        Book returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(commentary -> commentary.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(entityManager.getEntityManager().contains(returnedBook)).isTrue();
    }

    @DisplayName("Должен сохранить измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Book expectedBook = new Book();
        expectedBook.setId(books.get(0).getId());
        expectedBook.setTitle("Updated_Book");
        expectedBook.setAuthor(authors.get(authors.size() - 1));
        expectedBook.setGenres(new ArrayList<>(genres.subList(0, 2))); //во избежание ConcurrentModificationException

        assertThat(entityManager.find(Book.class, expectedBook.getId()))
                .isNotNull()
                .isNotEqualTo(expectedBook);

        Book returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(entityManager.getEntityManager().contains(returnedBook)).isTrue();
    }

    @DisplayName("Должен удалить книгу по id")
    @Test
    void shouldDeleteBook() {
        long targetId = books.get(0).getId();
        assertThat(entityManager.find(Book.class, targetId)).isNotNull();
        bookRepository.deleteById(targetId);
        assertThat(entityManager.find(Book.class, targetId)).isNull();
    }
}
