package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями книг")
@DataJpaTest
@Import(JpaCommentaryRepository.class)
public class JpaCommentaryRepositoryTest {

    @Autowired
    private JpaCommentaryRepository commentaryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Book> books;

    private List<Commentary> commentaries;

    @BeforeEach
    void setUp() {
        List<Author> authors = TestDataGenerator.generateAuthors();
        authors.forEach(entityManager::persist);

        List<Genre> genres = TestDataGenerator.generateGenres();
        genres.forEach(entityManager::persist);

        books = TestDataGenerator.generateBooks(authors, genres);
        books.forEach(entityManager::persist);

        commentaries = TestDataGenerator.generateCommentaries(books);
        commentaries.forEach(entityManager::persist);

        entityManager.flush();
    }

    @DisplayName("Должен загружать список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentariesListByBookId() {
        long targetBookId = books.get(0).getId();

        var actualCommentaries = commentaryRepository.findAllByBookId(targetBookId);
        var expectedCommentaries = commentaries.stream().filter(c -> c.getBook().getId() == targetBookId).toList();

        assertThat(actualCommentaries).containsExactlyElementsOf(expectedCommentaries);
        actualCommentaries.forEach(System.out::println);
    }

    @DisplayName("Должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentaryById() {
        long targetId = commentaries.get(0).getId();

        Optional<Commentary> optionalActualCommentary = commentaryRepository.findById(targetId);
        Commentary expectedCommentary = entityManager.find(Commentary.class, targetId);

        assertThat(optionalActualCommentary).isPresent()
                .get()
                .isEqualTo(expectedCommentary);
    }

    @DisplayName("Должен сохранять новый комментарий")
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

    @DisplayName("Должен сохранять измененный комментарий")
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

    @DisplayName("Должен удалять комментарий по id")
    @Test
    void shouldDeleteCommentary() {
        long targetId = commentaries.get(0).getId();
        assertThat(entityManager.find(Commentary.class, targetId)).isNotNull();
        commentaryRepository.deleteById(targetId);
        assertThat(entityManager.find(Commentary.class, targetId)).isNull();
    }
}
