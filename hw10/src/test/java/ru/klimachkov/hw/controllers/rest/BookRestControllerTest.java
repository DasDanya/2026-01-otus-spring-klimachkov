package ru.klimachkov.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.BookService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Эндпоинты для работы с книгами")
@WebMvcTest(BookRestController.class)
public class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @DisplayName("GET /api/books должен вернуть непустой JSON с книгами")
    @Test
    void shouldReturnNonEmptyJsonWithBooks() throws Exception {
        List<BookDto> books = LongStream.range(1, 4).mapToObj(this::generateBook).toList();

        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(books.size())))
                .andExpectAll(
                        IntStream.range(0, books.size())
                            .mapToObj(i -> JsonMatchers.getBookJsonMatchersStream(books.get(i), "[%d]".formatted(i)))
                            .flatMap(Function.identity())
                            .toArray(ResultMatcher[]::new)
                );

        verify(bookService).findAll();
    }

    @DisplayName("GET /api/books должен вернуть пустой JSON")
    @Test
    void shouldReturnEmptyJsonWithoutBooks() throws Exception {
        when(bookService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookService).findAll();
    }

    @DisplayName("GET /api/books/{id} должен вернуть JSON с книгой")
    @Test
    void shouldReturnJsonWithBook() throws Exception {
        BookDto book = generateBook();

        when(bookService.findById(anyLong())).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(getBookJsonMatchers(book));

        verify(bookService).findById(book.getId());
    }

    @DisplayName("GET /api/books/{id} должен вернуть 404, если книга не найдена")
    @Test
    void shouldReturn404WhenViewBookNotFound() throws Exception {
        long nonExistentBookId = 0L;

        when(bookService.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/{id}", nonExistentBookId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").exists());

        verify(bookService).findById(nonExistentBookId);
    }

    @DisplayName("POST /api/books должен вставить книгу")
    @Test
    void shouldReturnJsonWithInsertedBook() throws Exception {
        BookDto book = generateBook();

        when(bookService.insert(anyString(), anyLong(), anySet())).thenReturn(book);

        Set<Long> bookGenresIds = book.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet());

        Map<String, Object> requestBody = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId(),
                "genresIds", bookGenresIds
        );

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(getBookJsonMatchers(book));

        verify(bookService).insert(book.getTitle(), book.getAuthor().getId(), bookGenresIds);
    }

    @DisplayName("POST /api/books при невалидном теле запроса не должен вставить книгу")
    @Test
    void shouldNotInsertBookByNotValidRequestBody() throws Exception {
        Map<String, Object> notValidRequestBody = Map.of(
                "title", "",
                "authorId", 1,
                "genreIds", Collections.emptySet()
        );

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidRequestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')].errors").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'genresIds')].errors").exists());

        verify(bookService, never()).insert(anyString(), anyLong(), anySet());
    }

    @DisplayName("PUT /api/books/{id} должен обновить книгу")
    @Test
    void shouldReturnJsonWithUpdatedBook() throws Exception {
        BookDto book = generateBook();

        when(bookService.update(anyLong(), anyString(), anyLong(), anySet())).thenReturn(book);

        Set<Long> bookGenresIds = book.getGenres().stream().map(GenreDto::getId).collect(Collectors.toSet());

        Map<String, Object> requestBody = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId(),
                "genresIds", bookGenresIds
        );

        mockMvc.perform(put("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(getBookJsonMatchers(book));

        verify(bookService).update(book.getId(), book.getTitle(), book.getAuthor().getId(), bookGenresIds);
    }

    @DisplayName("PUT /api/books/{id} при невалидном теле запроса не должен обновить книгу")
    @Test
    void shouldNotUpdateBookByNotValidRequestBody() throws Exception {
        long bookId = 1L;

        Map<String, Object> notValidRequestBody = Map.of(
                "title", "",
                "authorId", 1,
                "genreIds", Collections.emptySet()
        );

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidRequestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')].errors").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'genresIds')].errors").exists());

        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), anySet());
    }

    @DisplayName("PUT /api/books/{id} должен вернуть 404 при обновлении книги с несуществующими связанными данными")
    @Test
    void shouldReturn404WhenEditBookBasedOnNonExistentRelatedData() throws Exception {
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

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(nullValue()));

        verify(bookService).update(bookId, title, nonExistentAuthorId, genresIds);
    }

    @DisplayName("DELETE /api/books/{id} должен удалить книгу")
    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(anyLong());

        long bookId = 1L;
        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());

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

    private ResultMatcher[] getBookJsonMatchers(@NonNull BookDto book) {
        return JsonMatchers.getBookJsonMatchersStream(book, "").toArray(ResultMatcher[]::new);
    }
}
