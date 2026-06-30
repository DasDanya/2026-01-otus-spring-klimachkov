package ru.klimachkov.hw.controllers.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.services.GenreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Эндпоинты для работы с жанрами книг")
@WebFluxTest(GenreRestController.class)
public class GenreRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    @DisplayName("GET /api/genres должен вернуть непустой JSON с жанрами")
    @Test
    void shouldReturnNonEmptyJsonWithGenres() {
        Flux<GenreDto> genres = Flux.range(1, 7).map(id -> new GenreDto(id, "Genre_" + id));

        when(genreService.findAll()).thenReturn(genres);

        webTestClient.get().uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(GenreDto.class)
                .consumeWith(result -> {
                    List<GenreDto> actualGenres = result.getResponseBody();
                    StepVerifier.create(genres.collectList())
                            .assertNext(expectedGenres ->
                                    assertThat(actualGenres).usingRecursiveComparison().isEqualTo(expectedGenres))
                            .verifyComplete();
                });

        verify(genreService).findAll();
    }

    @DisplayName("GET /api/genres должен вернуть пустой JSON")
    @Test
    void shouldReturnEmptyJsonWithoutGenres() {
        when(genreService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(GenreDto.class)
                .hasSize(0);

        verify(genreService).findAll();
    }

}
