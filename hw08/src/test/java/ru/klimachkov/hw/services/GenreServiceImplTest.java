package ru.klimachkov.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами книг")
@DataMongoTest
@Import(GenreServiceImpl.class)
public class GenreServiceImplTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreService genreService;

    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = TestDataGenerator.generateGenres();
        genreRepository.saveAll(genres);
    }

    @DisplayName("Должен загрузить список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        List<Genre> actualGenres = genreService.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(genres);
    }
}
