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
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.CommentaryService;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Эндпоинты для работы с комментариями")
@WebFluxTest(CommentaryRestController.class)
public class CommentaryRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentaryService commentaryService;

    @Test
    @DisplayName("GET /api/books/{bookId}/commentaries должен вернуть непустой JSON с комментариями, " +
            "содержащих информацию о книге")
    void shouldReturnNonEmptyJsonWithCommentariesContainingInfoAboutBook() {
        BookDto book = generateBook();
        List<CommentaryDto> expectedCommentaries = IntStream.range(1, 4)
                .mapToObj(id -> generateCommentary(id, book))
                .toList();

        when(commentaryService.findByBookId(anyLong())).thenReturn(Flux.fromIterable(expectedCommentaries));

        webTestClient.get().uri("/api/books/{bookId}/commentaries", book.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CommentaryDto.class)
                .hasSize(expectedCommentaries.size())
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison()
                        .isEqualTo(expectedCommentaries));

        verify(commentaryService).findByBookId(book.getId());
    }

    @DisplayName("GET /api/books/{bookId}/commentaries должен вернуть пустой JSON с комментариями книги")
    @Test
    void shouldReturnEmptyJsonWithoutBookCommentaries() {
        when(commentaryService.findByBookId(anyLong())).thenReturn(Flux.empty());

        long nonExistentBookId = 0L;

        webTestClient.get().uri("/api/books/{bookId}/commentaries", nonExistentBookId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CommentaryDto.class)
                .hasSize(0);

        verify(commentaryService).findByBookId(nonExistentBookId);
    }

    @Test
    @DisplayName("GET /api/books/{bookId}/commentaries/only должен вернуть непустой JSON с комментариями, " +
            "не содержащих информацию о книге")
    void shouldReturnNonEmptyJsonWithCommentariesNotContainingInfoAboutBook() {
        BookDto book = generateBook();
        List<CommentaryDto> expectedCommentaries = IntStream.range(1, 4)
                .mapToObj(id -> generateCommentary(id, null))
                .toList();

        when(commentaryService.findByBookIdWithoutFetch(anyLong())).thenReturn(Flux.fromIterable(expectedCommentaries));

        webTestClient.get().uri("/api/books/{bookId}/commentaries/only", book.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CommentaryDto.class)
                .hasSize(expectedCommentaries.size())
                .consumeWith(result -> {
                    List<CommentaryDto> actualCommentaries = result.getResponseBody();
                    assertThat(actualCommentaries)
                            .extracting(CommentaryDto::getBook)
                            .containsOnlyNulls();

                    assertThat(actualCommentaries).usingRecursiveComparison().isEqualTo(expectedCommentaries);
                });

        verify(commentaryService).findByBookIdWithoutFetch(book.getId());
    }

    @DisplayName("GET /api/books/{bookId}/commentaries/only должен вернуть пустой JSON с комментариями книги")
    @Test
    void shouldReturnEmptyJsonWithoutBookCommentariesOnly() {
        when(commentaryService.findByBookIdWithoutFetch(anyLong())).thenReturn(Flux.empty());

        long nonExistentBookId = 0L;

        webTestClient.get().uri("/api/books/{bookId}/commentaries/only", nonExistentBookId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CommentaryDto.class)
                .hasSize(0);

        verify(commentaryService).findByBookIdWithoutFetch(nonExistentBookId);
    }

    @DisplayName("GET /api/books/{bookId}/commentaries/{id} должен вернуть JSON с комментарием")
    @Test
    void shouldReturnJsonWithCommentary() {
        CommentaryDto expectedCommentary = generateCommentary();

        when(commentaryService.findById(anyLong())).thenReturn(Mono.just(expectedCommentary));

        webTestClient.get().uri("/api/books/{bookId}/commentaries/{id}",
                        expectedCommentary.getBook().getId(), expectedCommentary.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentaryDto.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison()
                        .isEqualTo(expectedCommentary));

        verify(commentaryService).findById(expectedCommentary.getId());
    }

    @DisplayName("GET /api/books/{bookId}/commentaries/{id} должен вернуть 404, если комментарий не найден")
    @Test
    void shouldReturn404WhenViewCommentaryNotFound() {
        long bookId = 1L;
        long nonExistentCommentaryId = 0L;

        when(commentaryService.findById(anyLong())).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/books/{bookId}/commentaries/{id}", bookId, nonExistentCommentaryId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errorMessage").exists();

        verify(commentaryService).findById(nonExistentCommentaryId);
    }

    @DisplayName("POST /api/books/{bookId}/commentaries должен вставить комментарий")
    @Test
    void shouldReturnJsonWithInsertedCommentary() {
        CommentaryDto commentary = generateCommentary();

        when(commentaryService.insert(anyString(), anyLong())).thenReturn(Mono.just(commentary));

        Map<String, Object> requestBody = Map.of("text", commentary.getText());

        webTestClient.post().uri("/api/books/{bookId}/commentaries", commentary.getBook().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentaryDto.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison()
                        .isEqualTo(commentary));

        verify(commentaryService).insert(commentary.getText(), commentary.getBook().getId());
    }

    @DisplayName("POST /api/books/{bookId}/commentaries при невалидном теле запроса не должен вставить комментарий")
    @Test
    void shouldNotInsertCommentaryByNotValidRequestBody() {
        long bookId = 1L;
        Map<String, Object> notValidRequestBody = Map.of("text", "");

        webTestClient.post().uri("/api/books/{bookId}/commentaries", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(notValidRequestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[?(@.field == 'text')].errors").exists();

        verify(commentaryService, never()).insert(anyString(), anyLong());
    }

    @DisplayName("PUT /api/books/{bookId}/commentaries/{id} должен обновить комментарий")
    @Test
    void shouldReturnJsonWithUpdatedCommentary() {
        CommentaryDto commentary = generateCommentary();

        when(commentaryService.update(anyLong(), anyString(), anyLong())).thenReturn(Mono.just(commentary));

        Map<String, Object> requestBody = Map.of("text", commentary.getText());

        webTestClient.put().uri("/api/books/{bookId}/commentaries/{id}",
                        commentary.getBook().getId(), commentary.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentaryDto.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).usingRecursiveComparison()
                        .isEqualTo(commentary));

        verify(commentaryService).update(commentary.getId(), commentary.getText(), commentary.getBook().getId());
    }

    @DisplayName("PUT /api/books/{bookId}/commentaries/{id} при невалидном теле запроса не должен обновить комментарий")
    @Test
    void shouldNotUpdateCommentaryByNotValidRequestBody() {
        long bookId = 1L;
        long commentaryId = 1L;

        Map<String, Object> notValidRequestBody = Map.of("text", "");

        webTestClient.put().uri("/api/books/{bookId}/commentaries/{id}", bookId, commentaryId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(notValidRequestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errors").exists()
                .jsonPath("$.errors").isArray()
                .jsonPath("$.errors[?(@.field == 'text')].errors").exists();

        verify(commentaryService, never()).update(anyLong(), anyString(), anyLong());
    }

    @Test
    @DisplayName("PUT /api/books/{bookId}/commentaries/{id} должен вернуть 404 при обновлении комментария " +
            "с несуществующими связанными данными")
    void shouldReturn404WhenEditCommentaryBasedOnNonExistentRelatedData() {
        when(commentaryService.update(anyLong(), anyString(), anyLong())).thenThrow(EntityNotFoundException.class);

        long commentaryId = 1L;
        String text = "Commentary";
        long nonExistentBookId = 0L;

        Map<String, Object> requestBody = Map.of("text", text);

        webTestClient.put().uri("/api/books/{bookId}/commentaries/{id}", nonExistentBookId, commentaryId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorMessage").value(nullValue());

        verify(commentaryService).update(commentaryId, text, nonExistentBookId);
    }

    @DisplayName("DELETE /api/books/{bookId}/commentaries/{id} должен удалить комментарий")
    @Test
    void shouldDeleteCommentary() {
        when(commentaryService.deleteById(anyLong())).thenReturn(Mono.empty());

        long bookId = 1L;
        long commentaryId = 1L;
        webTestClient.delete().uri("/api/books/{bookId}/commentaries/{id}", bookId, commentaryId)
                .exchange()
                .expectStatus().isNoContent();

        verify(commentaryService).deleteById(commentaryId);
    }

    private BookDto generateBook() {
        BookDto book = new BookDto();
        book.setId(1L);
        book.setTitle("BookTitle_1");
        book.setAuthor(new AuthorDto(1L, "Author_1"));
        book.setGenres(List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2")));

        return book;
    }

    private CommentaryDto generateCommentary() {
        return generateCommentary(1L, generateBook());
    }

    private CommentaryDto generateCommentary(long id, BookDto book) {
        return new CommentaryDto(id, "Commentary_" + id, book);
    }
}
