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
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.CommentaryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
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

@DisplayName("Эндпоинты для работы с комментариями")
@WebMvcTest(CommentaryRestController.class)
public class CommentaryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentaryService commentaryService;

    @DisplayName("GET /api/commentaries/{id} должен вернуть JSON с комментарием")
    @Test
    void shouldReturnJsonWithCommentary() throws Exception {
        CommentaryDto commentary = generateCommentary();

        when(commentaryService.findById(anyLong())).thenReturn(Optional.of(commentary));

        mockMvc.perform(get("/api/commentaries/{id}", commentary.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(getCommentaryJsonMatchers(commentary));

        verify(commentaryService).findById(commentary.getId());
    }

    @DisplayName("GET /api/commentaries/{id} должен вернуть 404, если комментарий не найден")
    @Test
    void shouldReturn404WhenViewCommentaryNotFound() throws Exception {
        long nonExistentCommentaryId = 0L;

        when(commentaryService.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/commentaries/{id}", nonExistentCommentaryId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").exists());

        verify(commentaryService).findById(nonExistentCommentaryId);
    }

    @DisplayName("POST /api/commentaries должен вставить комментарий")
    @Test
    void shouldReturnJsonWithInsertedCommentary() throws Exception {
        CommentaryDto commentary = generateCommentary();

        when(commentaryService.insert(anyString(), anyLong())).thenReturn(commentary);

        Map<String, Object> requestBody = Map.of(
                "text", commentary.getText(),
                "bookId", commentary.getBook().getId()
        );

        mockMvc.perform(post("/api/commentaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(getCommentaryJsonMatchers(commentary));

        verify(commentaryService).insert(commentary.getText(), commentary.getBook().getId());
    }

    @DisplayName("POST /api/commentaries при невалидном теле запроса не должен вставить комментарий")
    @Test
    void shouldNotInsertCommentaryByNotValidRequestBody() throws Exception {
        Map<String, Object> notValidRequestBody = Map.of(
                "text", "",
                "bookId", 1
        );

        mockMvc.perform(post("/api/commentaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidRequestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").exists());

        verify(commentaryService, never()).insert(anyString(), anyLong());
    }

    @DisplayName("PUT /api/commentaries/{id} должен обновить комментарий")
    @Test
    void shouldReturnJsonWithUpdatedCommentary() throws Exception {
        CommentaryDto commentary = generateCommentary();

        when(commentaryService.update(anyLong(), anyString(), anyLong())).thenReturn(commentary);

        Map<String, Object> requestBody = Map.of(
                "text", commentary.getText(),
                "bookId", commentary.getBook().getId()
        );

        mockMvc.perform(put("/api/commentaries/{id}", commentary.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(getCommentaryJsonMatchers(commentary));

        verify(commentaryService).update(commentary.getId(),commentary.getText(), commentary.getBook().getId());
    }

    @DisplayName("PUT /api/commentaries/{id} при невалидном теле запроса не должен обновить комментарий")
    @Test
    void shouldNotUpdateCommentaryByNotValidRequestBody() throws Exception {
        long commentaryId = 1L;

        Map<String, Object> notValidRequestBody = Map.of(
                "text", "",
                "bookId", 1
        );

        mockMvc.perform(put("/api/commentaries/{id}", commentaryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notValidRequestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").exists());

        verify(commentaryService, never()).update(anyLong(), anyString(), anyLong());
    }

    @Test
    @DisplayName("PUT /api/commentaries/{id} должен вернуть 404 при обновлении комментария " +
            "с несуществующими связанными данными")
    void shouldReturn404WhenEditCommentaryBasedOnNonExistentRelatedData() throws Exception {
        when(commentaryService.update(anyLong(), anyString(), anyLong())).thenThrow(EntityNotFoundException.class);

        long commentaryId = 1L;
        String text = "Commentary";
        long nonExistentBookId = 0L;

        Map<String, Object> requestBody = Map.of(
                "text", text,
                "bookId", nonExistentBookId
        );

        mockMvc.perform(put("/api/commentaries/{id}", commentaryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(nullValue()));

        verify(commentaryService).update(commentaryId, text, nonExistentBookId);
    }

    @DisplayName("DELETE /api/commentaries/{id} должен удалить комментарий")
    @Test
    void shouldDeleteCommentary() throws Exception {
        doNothing().when(commentaryService).deleteById(anyLong());

        long commentaryId = 1L;
        mockMvc.perform(delete("/api/commentaries/{id}", commentaryId))
                .andExpect(status().isNoContent());

        verify(commentaryService).deleteById(commentaryId);
    }

    private CommentaryDto generateCommentary() {
        BookDto book = new BookDto();
        book.setId(1L);
        book.setTitle("BookTitle_1");
        book.setAuthor(new AuthorDto(1L, "Author_1"));
        book.setGenres(List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2")));

        return new CommentaryDto(1L, "Commentary_1", book);
    }

    private ResultMatcher[] getCommentaryJsonMatchers(@NonNull CommentaryDto commentary) {
        return JsonMatchers.getCommentaryJsonMatchersStream(commentary, "", true)
                .toArray(ResultMatcher[]::new);
    }
}
