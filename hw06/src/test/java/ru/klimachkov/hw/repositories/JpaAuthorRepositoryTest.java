package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Author;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с авторами книг")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    @Autowired
    private JpaAuthorRepository authorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = TestDataGenerator.generateAuthors();
        authors.forEach(entityManager::persist);
        entityManager.flush();
    }

    @DisplayName("Должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        List<Author> actualAuthors = authorRepository.findAll();
        assertThat(actualAuthors).containsExactlyElementsOf(authors);
        actualAuthors.forEach(System.out::println);
    }

    @DisplayName("Должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        long targetAuthorId = authors.get(0).getId();
        Optional<Author> optionalActualAuthor = authorRepository.findById(targetAuthorId);
        Author expectedAuthor = entityManager.find(Author.class, targetAuthorId);

        assertThat(optionalActualAuthor).isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }
}
