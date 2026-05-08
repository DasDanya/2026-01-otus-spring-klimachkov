package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Author;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с авторами книг")
@JdbcTest
@Import(JdbcAuthorRepository.class)
public class JdbcAuthorRepositoryTest {
    @Autowired
    private JdbcAuthorRepository repositoryJdbc;

    private static List<Author> dbAuthors;

    @BeforeAll
    static void init() {
        dbAuthors = TestDataGenerator.generateAuthors();
    }

    @DisplayName("Должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = repositoryJdbc.findAll();
        assertThat(actualAuthors).containsExactlyElementsOf(dbAuthors);
        actualAuthors.forEach(System.out::println);
    }

    @DisplayName("Должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("authorsProvider")
    void shouldReturnCorrectAuthorById(Author expectedAuthor) {
        var actualAuthor = repositoryJdbc.findById(expectedAuthor.getId());
        assertThat(actualAuthor).isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }

    private static Stream<Author> authorsProvider() {
        return dbAuthors.stream();
    }
}
