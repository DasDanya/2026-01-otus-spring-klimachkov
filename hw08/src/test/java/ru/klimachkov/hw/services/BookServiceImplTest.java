package ru.klimachkov.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.repositories.AuthorRepository;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.CommentaryRepository;
import ru.klimachkov.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с книгами")
@DataMongoTest
@Import(BookServiceImpl.class)
public class BookServiceImplTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private BookService bookService;

    private List<Author> authors;

    private List<Genre> genres;

    private List<Book> books;

    @BeforeEach
    void setUp() {
        authors = TestDataGenerator.generateAuthors();
        authorRepository.saveAll(authors);

        genres = TestDataGenerator.generateGenres();
        genreRepository.saveAll(genres);

        books = TestDataGenerator.generateBooks(authors, genres);
        bookRepository.saveAll(books);
    }

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
        genreRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @DisplayName("Должен загрузить список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookService.findAll();
        assertThat(actualBooks).isNotEmpty().hasSize(books.size());
        for (int i = 0; i < actualBooks.size(); i++) {
            AssertUtil.assertEqual(actualBooks.get(i), books.get(i));
        }
    }

    @DisplayName("Должен загрузить книгу по Id")
    @Test
    void shouldReturnCorrectBookById() {
        Book expectedBook = books.get(0);

        Optional<Book> actualBook = bookService.findById(expectedBook.getId());
        assertThat(actualBook).isPresent();

        AssertUtil.assertEqual(actualBook.get(), expectedBook);
    }

    @DisplayName("Должен вернуть пустой Optional книги по несуществующему Id")
    @Test
    void shouldReturnEmptyBookOptionalByNonExistentBookId() {
        Optional<Book> actualBook = bookService.findById("NonExistentBookId");
        assertThat(actualBook).isEmpty();
    }

    @DisplayName("Должен вставить новую книгу")
    @Test
    void shouldInsertNewBook() {
        String bookTitle = "NewBook";
        Author author = authors.get(0);
        List<Genre> bookGenres = genres.stream().limit(3).toList();
        Set<String> bookGenresIds = bookGenres.stream().map(Genre::getId).collect(Collectors.toSet());

        Book insertedBook = bookService.insert(bookTitle, author.getId(), bookGenresIds);
        AssertUtil.assertSuccessSave(insertedBook, bookTitle, author, bookGenres);

        assertThat(bookRepository.findById(insertedBook.getId())).isPresent();
    }

    @DisplayName("Должен выбросить исключение при попытке создания книги по несуществующему Id автора")
    @Test
    void shouldThrowWhenInsertNewBookByNonExistentAuthorId() {
        String nonExistentAuthorId = "NonExistentAuthorId";
        Set<String> bookGenresIds = genres.stream().map(Genre::getId).limit(2).collect(Collectors.toSet());

        assertThatThrownBy(() -> bookService.insert("NotInsertedBook", nonExistentAuthorId, bookGenresIds))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Должен выбросить исключение при попытке создания книги по несуществующим Id жанров")
    @Test
    void shouldThrowWhenInsertNewBookByNonExistentGenreIds() {
        Set<String> nonExistentGenreIds = Set.of("NonExistentGenre1", "NonExistentGenre2");
        assertThatThrownBy(() -> bookService.insert("NotInsertedBook", authors.get(0).getId(),
                nonExistentGenreIds)).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Должен обновить книгу")
    @Test
    void shouldUpdateBook() {
        Book bookBeforeUpdate = books.get(0);

        String updateTitle = "UpdatedBook";
        Author updateAuthor = authors.get(authors.size() - 1);
        List<Genre> updateBookGenres = genres.stream().limit(3).toList();
        Set<String> bookGenresIds = updateBookGenres.stream().map(Genre::getId).collect(Collectors.toSet());

        Book updatedBook = bookService.update(
                bookBeforeUpdate.getId(), updateTitle, updateAuthor.getId(), bookGenresIds);

        AssertUtil.assertSuccessSave(updatedBook, updateTitle, updateAuthor, updateBookGenres);

        assertThat(updatedBook.getId()).isEqualTo(bookBeforeUpdate.getId());
        assertThat(updatedBook.getTitle()).isNotEqualTo(bookBeforeUpdate.getTitle());
    }

    @DisplayName("Должен удалить книгу")
    @Test
    void shouldDeleteBook() {
        Book bookToDelete = books.get(0);
        List<Commentary> commentaries = IntStream.range(1, 4)
                .mapToObj(i -> new Commentary(null, "Commentary_" + i, bookToDelete))
                .toList();

        commentaryRepository.saveAll(commentaries);

        bookService.deleteById(bookToDelete.getId());
        assertThat(bookRepository.findById(bookToDelete.getId())).isEmpty();
        assertThat(commentaryRepository.findByBookId(bookToDelete.getId())).isEmpty();
    }

}
