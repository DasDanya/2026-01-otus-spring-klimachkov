package ru.klimachkov.hw.controllers.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.services.AuthorService;

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

@DisplayName("Эндпоинты для работы с авторами книг")
@WebMvcTest(AuthorRestController.class)
public class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @DisplayName("GET /api/authors должен вернуть непустой JSON с авторами")
    @Test
    void shouldReturnNonEmptyJsonWithAuthors() throws Exception {
        List<AuthorDto> authors = IntStream.range(1, 4)
                .mapToObj(id -> new AuthorDto(id, "Author_" + id))
                .toList();

        when(authorService.findAll()).thenReturn(authors);

        mockMvc.perform(get("/api/authors"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(authors.size())))
            .andExpectAll(
                    IntStream.range(0, authors.size())
                        .mapToObj(i -> JsonMatchers.getAuthorJsonMatchersStream(authors.get(i), "[%d]".formatted(i)))
                        .flatMap(Function.identity())
                        .toArray(ResultMatcher[]::new)
            );

        verify(authorService).findAll();
    }

    @DisplayName("GET /api/authors должен вернуть пустой JSON")
    @Test
    void shouldReturnEmptyJsonWithoutAuthors() throws Exception {
        when(authorService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(authorService).findAll();
    }
}
