package ru.klimachkov.hw.controllers.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.services.GenreService;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Эндпоинты для работы с жанрами книг")
@WebMvcTest(GenreRestController.class)
public class GenreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @DisplayName("GET /api/genres должен вернуть непустой JSON с жанрами")
    @Test
    void shouldReturnNonEmptyJsonWithGenres() throws Exception {
        List<GenreDto> genres = IntStream.range(1, 7)
                .mapToObj(id -> new GenreDto(id, "Genre_" + id))
                .toList();

        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(genres.size())))
                .andExpectAll(
                        IntStream.range(0, genres.size())
                            .mapToObj(i -> JsonMatchers.getGenreJsonMatchersStream(genres.get(i), "[%d]".formatted(i)))
                            .flatMap(Function.identity())
                            .toArray(ResultMatcher[]::new)
                );

        verify(genreService).findAll();
    }

    @DisplayName("GET /api/genres должен вернуть пустой JSON")
    @Test
    void shouldReturnEmptyJsonWithoutGenres() throws Exception {
        when(genreService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(genreService).findAll();
    }

}
