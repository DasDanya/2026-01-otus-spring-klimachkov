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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с комментариями книг")
@DataMongoTest
@Import(CommentaryServiceImpl.class)
public class CommentaryServiceImplTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private CommentaryService commentaryService;

    private List<Book> books;

    private List<Commentary> commentaries;

    @BeforeEach
    void setUp() {
        List<Author> authors = TestDataGenerator.generateAuthors();
        authorRepository.saveAll(authors);

        List<Genre> genres = TestDataGenerator.generateGenres();
        genreRepository.saveAll(genres);

        books = TestDataGenerator.generateBooks(authors, genres);
        bookRepository.saveAll(books);

        commentaries = TestDataGenerator.generateCommentaries(books);
        commentaryRepository.saveAll(commentaries);
    }

    @AfterEach
    void cleanUp() {
        commentaryRepository.deleteAll();
        bookRepository.deleteAll();
        genreRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @DisplayName("Должен загрузить комментарий по id")
    @Test
    void shouldReturnCorrectCommentaryById() {
        Commentary expectedCommentary = commentaries.get(0);

        Optional<Commentary> actualCommentary = commentaryRepository.findById(expectedCommentary.getId());
        assertThat(actualCommentary).isPresent();

        AssertUtil.assertEqual(actualCommentary.get(), expectedCommentary);
    }

    @DisplayName("Должен вернуть пустой Optional комментария по несуществующему Id")
    @Test
    void shouldReturnEmptyCommentaryOptionalByNonExistentCommentaryId() {
        Optional<Commentary> actualCommentary = commentaryService.findById("NonExistentCommentaryId");
        assertThat(actualCommentary).isEmpty();
    }

    @DisplayName("Должен загрузить список всех комментариев по Id книги")
    @Test
    void shouldReturnCorrectCommentariesListByBookId() {
        String targetBookId = books.get(0).getId();

        List<Commentary> expectedCommentaries = commentaries.stream()
                .filter(c -> c.getBook().getId().equals(targetBookId))
                .toList();

        List<Commentary> actualCommentaries = commentaryService.findByBookId(targetBookId);
        assertThat(actualCommentaries).isNotEmpty().hasSize(expectedCommentaries.size());

        for (int i = 0; i < actualCommentaries.size(); i++) {
            AssertUtil.assertEqual(actualCommentaries.get(i), expectedCommentaries.get(i));
        }
    }

    @DisplayName("Должен вставить новый комментарий")
    @Test
    void shouldInsertNewCommentary() {
        String commentaryText = "NewCommentary";
        Book book = books.get(0);

        Commentary insertedCommentary = commentaryService.insert(commentaryText, book.getId());

        AssertUtil.assertSuccessSave(insertedCommentary, commentaryText, book);
        assertThat(commentaryRepository.findById(insertedCommentary.getId())).isPresent();
    }

    @DisplayName("Должен выбросить исключение при попытке создания комментария по несуществующему Id книги")
    @Test
    void shouldThrowWhenInsertNewCommentaryByNonExistentBookId() {
        String nonExistentBookId = "NonExistentBookId";

        assertThatThrownBy(() -> commentaryService.insert("NotInsertedCommentary", nonExistentBookId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Должен обновить комментарий")
    @Test
    void shouldUpdateCommentary() {
        Commentary commentaryBeforeUpdate = commentaries.get(0);

        String updateText = "UpdatedCommentary";
        Book updateBook = books.get(books.size() - 1);

        Commentary updatedCommentary = commentaryService.update(
                commentaryBeforeUpdate.getId(), updateText, updateBook.getId());

        AssertUtil.assertSuccessSave(updatedCommentary, updateText, updateBook);

        assertThat(updatedCommentary.getId()).isEqualTo(commentaryBeforeUpdate.getId());
        assertThat(updatedCommentary.getText()).isNotEqualTo(commentaryBeforeUpdate.getText());
    }

    @DisplayName("Должен удалить комментарий")
    @Test
    void shouldDeleteCommentary() {
        String commentaryToDeleteId = commentaries.get(0).getId();

        commentaryService.deleteById(commentaryToDeleteId);
        assertThat(commentaryRepository.findById(commentaryToDeleteId)).isEmpty();
    }
}
