package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с жанрами книг")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = TestDataGenerator.generateGenres();
        genres.forEach(entityManager::persist);
        entityManager.flush();
    }

    @DisplayName("Должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        List<Genre> actualGenres = genreRepository.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(genres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("Должен загружать список жанров по указанному набору Id жанров")
    @Test
    void shouldReturnCorrectGenresListByIdsSet() {
        List<Genre> expectedGenres = genres.subList(0, 2);
        Set<Long> expectedGenresIds = expectedGenres.stream().map(Genre::getId).collect(Collectors.toSet());

        var actualGenres = genreRepository.findAllByIds(expectedGenresIds);
        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
        actualGenres.forEach(System.out::println);
    }
}
