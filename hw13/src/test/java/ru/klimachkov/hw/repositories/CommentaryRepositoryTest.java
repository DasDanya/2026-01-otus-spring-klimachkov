package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.klimachkov.hw.TestDataCreator;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с комментариями книг")
@DataJpaTest
public class CommentaryRepositoryTest {

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Book> books;

    private List<Commentary> commentaries;

    @BeforeEach
    void setUp() {
        List<Author> authors = TestDataCreator.createAuthors();
        authors.forEach(entityManager::persist);

        List<Genre> genres = TestDataCreator.createGenres();
        genres.forEach(entityManager::persist);

        books = TestDataCreator.createBooks(authors, genres);
        books.forEach(entityManager::persist);

        commentaries = TestDataCreator.createCommentaries(books);
        commentaries.forEach(entityManager::persist);

        entityManager.flush();
    }

    @DisplayName("Должен загрузить список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentariesListByBookId() {
        long targetBookId = books.get(0).getId();

        var actualCommentaries = commentaryRepository.findByBookId(targetBookId);
        var expectedCommentaries = commentaries.stream().filter(c -> c.getBook().getId() == targetBookId).toList();

        assertThat(actualCommentaries).containsExactlyElementsOf(expectedCommentaries);
    }

    @DisplayName("Должен загрузить список всех комментариев по id книги без fetch")
    @Test
    void shouldReturnCorrectCommentariesListWithoutFetchByBookId() {
        long targetBookId = books.get(0).getId();

        var actualCommentaries = commentaryRepository.findByBookIdWithoutFetch(targetBookId);
        var expectedCommentaries = commentaries.stream().filter(c -> c.getBook().getId() == targetBookId).toList();

        assertThat(actualCommentaries)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedCommentaries);
    }

    @DisplayName("Должен загрузить комментарий по id")
    @Test
    void shouldReturnCorrectCommentaryById() {
        long targetId = commentaries.get(0).getId();

        Optional<Commentary> optionalActualCommentary = commentaryRepository.findById(targetId);
        Commentary expectedCommentary = entityManager.find(Commentary.class, targetId);

        assertThat(optionalActualCommentary).isPresent()
                .get()
                .isEqualTo(expectedCommentary);
    }

    @DisplayName("Должен сохранить новый комментарий")
    @Test
    void shouldSaveNewCommentary() {
        Commentary expectedCommentary = new Commentary();
        expectedCommentary.setText("New_Commentary");
        expectedCommentary.setBook(books.get(0));

        Commentary returnedCommentary = commentaryRepository.save(expectedCommentary);

        assertThat(returnedCommentary).isNotNull()
                .matches(commentary -> commentary.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedCommentary);

        assertThat(entityManager.getEntityManager().contains(returnedCommentary)).isTrue();
    }

    @DisplayName("Должен сохранить измененный комментарий")
    @Test
    void shouldSaveUpdatedCommentary() {
        Commentary expectedCommentary = new Commentary();
        expectedCommentary.setId(commentaries.get(0).getId());
        expectedCommentary.setText("Updated_Commentary");
        expectedCommentary.setBook(books.get(books.size() - 1));

        assertThat(entityManager.find(Commentary.class, expectedCommentary.getId()))
                .isNotNull()
                .isNotEqualTo(expectedCommentary);

        Commentary returnedCommentary = commentaryRepository.save(expectedCommentary);
        assertThat(returnedCommentary).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedCommentary);

        assertThat(entityManager.getEntityManager().contains(returnedCommentary)).isTrue();
    }

    @DisplayName("Должен удалить комментарий по id")
    @Test
    void shouldDeleteCommentary() {
        long targetId = commentaries.get(0).getId();
        assertThat(entityManager.find(Commentary.class, targetId)).isNotNull();
        commentaryRepository.deleteById(targetId);
        assertThat(entityManager.find(Commentary.class, targetId)).isNull();
    }
}
