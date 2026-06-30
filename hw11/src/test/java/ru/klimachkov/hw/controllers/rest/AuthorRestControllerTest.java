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
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.services.AuthorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Эндпоинты для работы с авторами книг")
@WebFluxTest(AuthorRestController.class)
public class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @DisplayName("GET /api/authors должен вернуть непустой JSON с авторами")
    @Test
    void shouldReturnNonEmptyJsonWithAuthors() {
        Flux<AuthorDto> authors = Flux.range(1, 4).map(id -> new AuthorDto(id, "Author_" + id));

        when(authorService.findAll()).thenReturn(authors);

        webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AuthorDto.class)
                .consumeWith(result -> {
                    List<AuthorDto> actualAuthors = result.getResponseBody();
                    StepVerifier.create(authors.collectList())
                            .assertNext(expectedAuthors ->
                                    assertThat(actualAuthors).usingRecursiveComparison().isEqualTo(expectedAuthors))
                            .verifyComplete();
                });

        verify(authorService).findAll();
    }

    @DisplayName("GET /api/authors должен вернуть пустой JSON")
    @Test
    void shouldReturnEmptyJsonWithoutAuthors() {
        when(authorService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(AuthorDto.class)
                .hasSize(0);

        verify(authorService).findAll();
    }
}
