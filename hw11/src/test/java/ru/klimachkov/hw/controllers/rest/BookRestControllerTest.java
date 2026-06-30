package ru.klimachkov.hw.controllers.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.BookService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Эндпоинты для работы с книгами")
@WebFluxTest(BookRestController.class)
public class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @DisplayName("GET /api/books должен вернуть непустой JSON с книгами")
    @Test
    void shouldReturnNonEmptyJsonWithBooks() {
        Flux<BookDto> books = Flux.range(1, 4).map(this::generateBook);

        when(bookService.findAll()).thenReturn(books);

        webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .consumeWith(result -> {
                    List<BookDto> actualBooks = result.getResponseBody();
                    StepVerifier.create(books.collectList())
                            .assertNext(expectedBooks ->
                                    assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks))
                            .verifyComplete();
                });

        verify(bookService).findAll();
    }

    @DisplayName("GET /api/books должен вернуть пустой JSON")
    @Test
    void shouldReturnEmptyJsonWithoutBooks() {
        when(bookService.findAll()).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .hasSize(0);

        verify(bookService).findAll();
    }

    @DisplayName("GET /api/books/{id} должен вернуть JSON с книгой")
    @Test
    void shouldReturnJsonWithBook() {
        BookDto expectedBook = generateBook();

        when(bookService.findById(anyLong())).thenReturn(Mono.just(expectedBook));

        webTestClient.get().uri("/api/books/{id}", expectedBook.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison()
                        .isEqualTo(expectedBook));

        verify(bookService).findById(expectedBook.getId());
    }

    @DisplayName("GET /api/books/{id} должен вернуть 404, если книга не найдена")
    @Test
    void shouldReturn404WhenViewBookNotFound() {
        long nonExistentBookId = 0L;

        when(bookService.findById(anyLong())).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/books/{id}", nonExistentBookId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errorMessage").exists();

        verify(bookService).findById(nonExistentBookId);
    }

    @DisplayName("POST /api/books должен вставить книгу")
    @Test
    void shouldReturnJsonWithInsertedBook() {
        BookDto book = generateBook();

        when(bookService.insert(anyString(), anyLong(), anySet())).thenReturn(Mono.just(book));

        Set<Long> bookGenresIds = book.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet());

        Map<String, Object> requestBody = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId(),
                "genresIds", bookGenresIds
        );

        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison().isEqualTo(book));

        verify(bookService).insert(book.getTitle(), book.getAuthor().getId(), bookGenresIds);
    }

    @DisplayName("POST /api/books при невалидном теле запроса не должен вставить книгу")
    @Test
    void shouldNotInsertBookByNotValidRequestBody() {
        Map<String, Object> notValidRequestBody = Map.of(
                "title", "",
                "authorId", 1,
                "genreIds", Collections.emptySet()
        );

        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(notValidRequestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[?(@.field == 'title')].errors").exists()
                .jsonPath("$.errors[?(@.field == 'genresIds')].errors").exists();

        verify(bookService, never()).insert(anyString(), anyLong(), anySet());
    }

    @DisplayName("PUT /api/books/{id} должен обновить книгу")
    @Test
    void shouldReturnJsonWithUpdatedBook() {
        BookDto book = generateBook();

        when(bookService.update(anyLong(), anyString(), anyLong(), anySet())).thenReturn(Mono.just(book));

        Set<Long> bookGenresIds = book.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet());

        Map<String, Object> requestBody = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId(),
                "genresIds", bookGenresIds
        );

        webTestClient.put().uri("/api/books/{id}", book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison().isEqualTo(book));

        verify(bookService).update(book.getId(), book.getTitle(), book.getAuthor().getId(), bookGenresIds);
    }

    @DisplayName("PUT /api/books/{id} при невалидном теле запроса не должен обновить книгу")
    @Test
    void shouldNotUpdateBookByNotValidRequestBody() {
        long bookId = 1L;

        Map<String, Object> notValidRequestBody = Map.of(
                "title", "",
                "authorId", 1,
                "genreIds", Collections.emptySet()
        );

        webTestClient.put().uri("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(notValidRequestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[?(@.field == 'title')].errors").exists()
                .jsonPath("$.errors[?(@.field == 'genresIds')].errors").exists();

        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), anySet());
    }

    @DisplayName("PUT /api/books/{id} должен вернуть 404 при обновлении книги с несуществующими связанными данными")
    @Test
    void shouldReturn404WhenEditBookBasedOnNonExistentRelatedData() {
        when(bookService.update(anyLong(), anyString(), anyLong(), anySet())).thenThrow(EntityNotFoundException.class);

        long bookId = 1L;
        String title = "BookTitle";
        long nonExistentAuthorId = 0L;
        Set<Long> genresIds = Set.of(1L, 2L);

        Map<String, Object> requestBody = Map.of(
                "title", title,
                "authorId", nonExistentAuthorId,
                "genresIds", genresIds
        );

        webTestClient.put().uri("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorMessage").value(nullValue());

        verify(bookService).update(bookId, title, nonExistentAuthorId, genresIds);
    }

    @DisplayName("DELETE /api/books/{id} должен удалить книгу")
    @Test
    void shouldDeleteBook() {
        when(bookService.deleteById(anyLong())).thenReturn(Mono.empty());

        long bookId = 1L;
        webTestClient.delete().uri("/api/books/{id}", bookId)
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService).deleteById(bookId);
    }

    private BookDto generateBook() {
        return generateBook(1L);
    }

    private BookDto generateBook(long id) {
        BookDto book = new BookDto();
        book.setId(id);
        book.setTitle("BookTitle_" + id);
        book.setAuthor(new AuthorDto(id, "Author_" + id));
        book.setGenres(List.of(
                new GenreDto(id, "Genre_" + id),
                new GenreDto(id + 1, "Genre_" + (id + 1)))
        );

        return book;
    }
}
