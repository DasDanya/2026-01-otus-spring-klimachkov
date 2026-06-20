package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.klimachkov.hw.TestDataGenerator;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами книг")
@DataJpaTest
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = TestDataGenerator.generateGenres();
        genres.forEach(entityManager::persist);
        entityManager.flush();
    }

    @DisplayName("Должен загрузить список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        List<Genre> actualGenres = genreRepository.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(genres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("Должен загрузить список жанров по указанному набору Id жанров")
    @Test
    void shouldReturnCorrectGenresListByIdsSet() {
        List<Genre> expectedGenres = genres.subList(0, 2);
        Set<Long> expectedGenresIds = expectedGenres.stream().map(Genre::getId).collect(Collectors.toSet());

        List<Genre> actualGenres = genreRepository.findByIdIn(expectedGenresIds);
        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }
}
