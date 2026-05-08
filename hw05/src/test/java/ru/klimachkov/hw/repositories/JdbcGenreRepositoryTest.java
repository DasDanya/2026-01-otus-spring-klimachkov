package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами книг")
@JdbcTest
@Import(JdbcGenreRepository.class)
public class JdbcGenreRepositoryTest {
    @Autowired
    private JdbcGenreRepository repositoryJdbc;

    private static List<Genre> dbGenres;

    @BeforeAll
    static void init() {
        dbGenres = TestDataGenerator.generateGenres();
    }

    @DisplayName("Должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repositoryJdbc.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(dbGenres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("Должен загружать список жанров по указанному набору Id жанров")
    @Test
    void shouldReturnCorrectGenresListByIdsSet() {
        long limit = new Random().nextLong(dbGenres.size()) + 1;
        var expectedGenres = dbGenres.stream().limit(limit).toList();
        Set<Long> expectedGenresIds = expectedGenres.stream().map(Genre::getId).collect(Collectors.toSet());

        var actualGenres = repositoryJdbc.findAllByIds(expectedGenresIds);
        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
        actualGenres.forEach(System.out::println);
    }
}
