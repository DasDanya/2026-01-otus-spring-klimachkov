package ru.klimachkov.hw.controllers;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.klimachkov.hw.TestDataGenerator;
import ru.klimachkov.hw.dto.BookFormDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.services.AuthorService;
import ru.klimachkov.hw.services.BookService;
import ru.klimachkov.hw.services.CommentaryService;
import ru.klimachkov.hw.services.GenreService;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@DisplayName("Эндпоинты работы с книгами")
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentaryService commentaryService;

    private final List<Book> books = TestDataGenerator.generateBooks(
            TestDataGenerator.generateAuthors(), TestDataGenerator.generateGenres());

    @DisplayName("GET /books должен открыть View /list со списком всех книг")
    @Test
    void shouldRenderBooksListWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", books));
    }

    @DisplayName("GET /books/{id}/view должен открыть View /view с книгой")
    @Test
    @SuppressWarnings("unchecked")
    void shouldRenderBookViewWithCorrectViewAndModelAttributes() throws Exception {
        Book viewBook = books.get(0);
        when(bookService.findById(anyLong())).thenReturn(Optional.of(viewBook));

        List<Commentary> commentaries = IntStream.range(1, 10)
                .mapToObj(id -> new Commentary(id, "Commentary_" + id, viewBook))
                .toList();

        when(commentaryService.findByBookIdWithoutFetch(viewBook.getId())).thenReturn(commentaries);

        List<CommentaryDto> bookCommentaries = commentaries.stream().map(CommentaryDto::of).toList();

        mockMvc.perform(get("/books/{id}/view", viewBook.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/view"))
                .andExpect(model().attributeExists("book", "commentaries"))
                .andExpect(model().attribute("book", viewBook))
                .andExpect(model().attribute("commentaries", bookCommentaries))
                .andExpect(content().string(allOf(
                        Stream.concat(
                            Stream.concat(
                                    Stream.of(
                                            containsString(String.valueOf(viewBook.getId())),
                                            containsString(viewBook.getTitle()),
                                            containsString(viewBook.getAuthor().getFullName())
                                    ),
                                    viewBook.getGenres().stream().map(genre -> containsString(genre.getName()))
                            ),
                            bookCommentaries.stream().map(commentary -> containsString(commentary.getText())))
                        .toArray(Matcher[]::new)))
                );
    }

    @DisplayName("GET /books/{id}/view должен вернуть View entity-not-found с кодом 404, если книга не найдена")
    @Test
    void shouldRenderEntityNotFoundWhenViewBookNotFound() throws Exception {
        when(bookService.findById(anyLong())).thenReturn(Optional.empty());

        long nonExistentBookId = 0L;
        mockMvc.perform(get("/books/{id}/view", nonExistentBookId))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/entity-not-found"));
    }

    @DisplayName("GET /books/add должен открыть форму с авторами и жанрами")
    @Test
    void shouldRenderAddViewWithCorrectViewAndModelAttributes() throws Exception {
        List<Author> expectedAuthors = TestDataGenerator.generateAuthors(true);
        List<Genre> expectedGenres = TestDataGenerator.generateGenres(true);

        when(authorService.findAll()).thenReturn(expectedAuthors);
        when(genreService.findAll()).thenReturn(expectedGenres);

        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("authors", "genres", "book"))
                .andExpect(model().attribute("authors", expectedAuthors))
                .andExpect(model().attribute("genres", expectedGenres))
                .andExpect(model().attribute("book", new BookFormDto()));
    }

    @DisplayName("POST /books/add должен вставить книгу и вернуться на страницу со всеми книгами")
    @Test
    void shouldInsertBookAndRedirectToBooksListView() throws Exception {
        when(bookService.insert(anyString(), anyLong(), anySet())).thenReturn(books.get(0));

        String title = "InsertBook";
        long authorId = 1L;
        Set<Long> genresIds = Set.of(1L, 2L, 3L);

        mockMvc.perform(post("/books/add")
                        .formField("title", title)
                        .formField("authorId", String.valueOf(authorId))
                        .formField("genresIds", genresIds.stream().map(String::valueOf).toArray(String[]::new)))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).insert(title, authorId, genresIds);
    }

    @DisplayName("POST /books/add должен при невалидных данных с формы вернуться в неё же и не выполнить вставку")
    @Test
    void shouldRedirectToEditFormWhenTryingToInsertInvalidBook() throws Exception {
        mockMvc.perform(post("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeHasFieldErrors("book", "title", "authorId", "genresIds"));

        verify(bookService, never()).insert(anyString(), anyLong(), anySet());
    }

    @DisplayName("GET /books/{id}/edit должен заполнить форму данными существующей книги")
    @Test
    void shouldRenderEditViewWithCorrectViewAndModelAttributes() throws Exception {
        List<Author> expectedAuthors = TestDataGenerator.generateAuthors(true);
        List<Genre> expectedGenres = TestDataGenerator.generateGenres(true);
        Book expectedBook = new Book(1L, "ExpectedBook", expectedAuthors.get(0), expectedGenres.subList(0, 2));

        when(authorService.findAll()).thenReturn(expectedAuthors);
        when(genreService.findAll()).thenReturn(expectedGenres);
        when(bookService.findById(anyLong())).thenReturn(Optional.of(expectedBook));

        mockMvc.perform(get("/books/{id}/edit", expectedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeExists("authors", "genres", "book"))
                .andExpect(model().attribute("authors", expectedAuthors))
                .andExpect(model().attribute("genres", expectedGenres))
                .andExpect(model().attribute("book", BookFormDto.of(expectedBook)))
                .andExpect(xpath("//input[@name='title']/@value").string(expectedBook.getTitle()))
                .andExpect(xpath("//select[@name='authorId']/option[@selected]/@value")
                        .string(String.valueOf(expectedBook.getAuthor().getId())))
                .andExpect(xpath("//select[@name='genresIds']/option[@selected]/@value")
                        .nodeCount(expectedBook.getGenres().size()));
    }

    @DisplayName("GET /books/{id}/edit должен вернуть View entity-not-found с кодом 404, если книга не найдена")
    @Test
    void shouldRenderEntityNotFoundWhenEditBookNotFound() throws Exception {
        when(bookService.findById(anyLong())).thenReturn(Optional.empty());

        long nonExistentBookId = 0L;
        mockMvc.perform(get("/books/{id}/edit", nonExistentBookId))
                .andExpect(status().isNotFound())
                .andExpect(view().name("errors/entity-not-found"));
    }

    @DisplayName("POST /books/{id}/edit должен обновить книгу и вернуться на страницу со всеми книгами")
    @Test
    void shouldEditBookAndRedirectToBooksListView() throws Exception {
        when(bookService.update(anyLong(), anyString(), anyLong(), anySet())).thenReturn(books.get(0));

        long bookId = 1L;
        String title = "UpdatedBook";
        long authorId = 1L;
        Set<Long> genresIds = Set.of(1L, 2L, 3L);

        mockMvc.perform(post("/books/{id}/edit", bookId)
                        .formField("title", title)
                        .formField("authorId", String.valueOf(authorId))
                        .formField("genresIds", genresIds.stream().map(String::valueOf).toArray(String[]::new)))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).update(bookId, title, authorId, genresIds);
    }

    @DisplayName("POST /books/{id}/edit должен при невалидных данных с формы вернуться в неё же и не выполнить обновление")
    @Test
    void shouldRedirectToEditFormWhenTryingToUpdateInvalidBook() throws Exception {
        long bookId = 1L;
        mockMvc.perform(post("/books/{id}/edit", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit"))
                .andExpect(model().attributeHasFieldErrors("book", "title", "authorId", "genresIds"));

        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), anySet());
    }

    @DisplayName("POST /books/{id}/delete должен удалить книгу и вернуться на страницу со всеми книгами")
    @Test
    void shouldDeleteBookAndRedirectToBooksListView() throws Exception {
        doNothing().when(bookService).deleteById(anyLong());

        long bookId = 1L;
        mockMvc.perform(post("/books/{id}/delete", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteById(bookId);
    }

    @DisplayName("GET /books/{id}/delete должен быть недоступен")
    @Test
    void shouldDeleteBookByGetMethodIsNotAllowed() throws Exception {
        long bookId = 1L;
        mockMvc.perform(get("/books/{id}/delete", bookId))
                .andExpect(status().isMethodNotAllowed());

        verify(bookService, never()).deleteById(bookId);
    }
}
