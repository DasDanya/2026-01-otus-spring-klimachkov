package ru.klimachkov.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.klimachkov.hw.controllers.AuthorController;
import ru.klimachkov.hw.controllers.BookController;
import ru.klimachkov.hw.controllers.CommentaryController;
import ru.klimachkov.hw.controllers.GenreController;
import ru.klimachkov.hw.handlers.GlobalAccessDeniedHandler;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.services.AuthorService;
import ru.klimachkov.hw.services.BookService;
import ru.klimachkov.hw.services.CommentaryService;
import ru.klimachkov.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тестирование интеграции Spring Security в приложение")
@WebMvcTest(controllers = {
        AuthorController.class,
        GenreController.class,
        BookController.class,
        CommentaryController.class
})
@Import({SecurityConfiguration.class, GlobalAccessDeniedHandler.class})
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentaryService commentaryService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @DisplayName("Страница входа в систему должна быть доступна без аутентификации")
    @Test
    void loginPageShouldNotRequireAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        allOf(
                            containsString("name=\"username\""),
                            containsString("name=\"password\""),
                            containsString("_csrf")
                        ))
                );
    }

    @DisplayName("При вводе неверных учетных данных пользователь должен остаться на странице входа в систему")
    @Test
    void shouldStayOnLoginPageWithErrorWhenInvalidCredentialsProvided() throws Exception {
        mockLoadUserByUsername();
        mockMvc.perform(formLogin("/login")
                        .user("wrongUser")
                        .password("wrongPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @DisplayName("При вводе корректных учетных данных пользователь должен быть перенаправлен на главную страницу")
    @Test
    void shouldRedirectToHomePageAfterSuccessfulLoginWithValidCredentials() throws Exception {
        mockLoadUserByUsername();
        mockMvc.perform(formLogin("/login")
                        .user("user")
                        .password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @DisplayName("Выход из системы успешно выполняется и перенаправляет на страницу входа в систему")
    @WithMockUser
    @Test
    void shouldRedirectToLoginPageAfterLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке открытия защищенной страницы должен быть " +
            "перенаправлен на страницу входа в систему")
    @ParameterizedTest
    @ValueSource(strings = {
            "/authors",
            "/genres",
            "/books",
            "/books/add",
            "/books/1",
            "/books/1/edit",
            "/books/1/commentaries",
            "/books/1/commentaries/add",
            "/books/1/commentaries/1",
            "/books/1/commentaries/1/edit"
    })
    void shouldRedirectToLoginPageWhenAccessingProtectedPageWithoutAuthentication(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке добавления книги должен быть перенаправлен " +
            "на станицу входа в систему")
    @Test
    void shouldRedirectToLoginWhenAddingBookWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/books/add")
                        .formField("title", "Book")
                        .formField("authorId", "1")
                        .formField("genresIds", "1", "2", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке редактирования книги должен быть перенаправлен " +
            "на станицу входа в систему")
    @Test
    void shouldRedirectToLoginWhenEditingBookWithoutAuthentication() throws Exception {
        long bookId = 1L;
        mockMvc.perform(post("/books/{id}/edit", bookId)
                        .formField("title", "Book")
                        .formField("authorId", "1")
                        .formField("genresIds", "1", "2", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке удаления книги должен быть перенаправлен " +
            "на станицу входа в систему")
    @Test
    void shouldRedirectToLoginWhenDeletingBookWithoutAuthentication() throws Exception {
        long bookId = 1L;
        mockMvc.perform(post("/books/{id}/delete", bookId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке добавления комментария должен быть перенаправлен " +
            "на станицу входа в систему")
    @Test
    void shouldRedirectToLoginWhenAddingCommentaryWithoutAuthentication() throws Exception {
        long bookId = 1L;
        mockMvc.perform(post("/books/{bookId}/commentaries/add", bookId)
                        .formField("text", "Commentary")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке редактирования комментария должен быть " +
            "перенаправлен на станицу входа в систему")
    @Test
    void shouldRedirectToLoginWhenEditingCommentaryWithoutAuthentication() throws Exception {
        long bookId = 1L;
        long commentaryId = 1L;
        mockMvc.perform(post("/books/{bookId}/commentaries/{id}/edit", bookId, commentaryId)
                        .formField("text", "Commentary")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Неаутентифицированный пользователь при попытке удаления комментария должен быть перенаправлен " +
            "на станицу входа в систему")
    @Test
    void shouldRedirectToLoginWhenDeletingCommentaryWithoutAuthentication() throws Exception {
        long bookId = 1L;
        long commentaryId = 1L;
        mockMvc.perform(post("/books/{bookId}/commentaries/{id}/delete", bookId, commentaryId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @DisplayName("Обычный аутентифицированный пользователь должен получить доступ к ряду страниц")
    @WithMockUser
    @ParameterizedTest
    @ValueSource(strings = {
            "/authors",
            "/genres",
            "/books",
            "/books/1",
            "/books/1/commentaries",
            "/books/1/commentaries/add",
            "/books/1/commentaries/1"
    })
    void shouldAllowAccessToSomePagesForRegularAuthenticatedUser(String url) throws Exception {
        mockAllFindMethodsOfServices();
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl(null));
    }

    @DisplayName("Обычный аутентифицированный пользователь должен перенаправиться на станицу, отображающую 403, " +
            "при попытке открытия админских страниц")
    @WithMockUser
    @ParameterizedTest
    @ValueSource(strings = {
            "/books/add",
            "/books/1/edit",
            "/books/1/commentaries/1/edit"
    })
    void shouldForwardToForbiddenPageWhenRegularUserAccessesAdminPages(String url) throws Exception {
        mockAllFindMethodsOfServices();
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/errors/forbidden"));
    }

    @DisplayName("Админ должен получить доступ ко всем страницам")
    @WithMockUser(authorities = "ADMIN")
    @ParameterizedTest
    @ValueSource(strings = {
            "/authors",
            "/genres",
            "/books",
            "/books/add",
            "/books/1",
            "/books/1/edit",
            "/books/1/commentaries",
            "/books/1/commentaries/add",
            "/books/1/commentaries/1",
            "/books/1/commentaries/1/edit"
    })
    void shouldAllowAccessToAllPagesForAdmin(String url) throws Exception {
        mockAllFindMethodsOfServices();
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl(null));
    }

    private void mockLoadUserByUsername() {
        UserDetails user = User.withUsername("user")
                .password("{noop}password")
                .authorities("USER")
                .build();

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(user);
    }

    private void mockAllFindMethodsOfServices() {
        Author author = new Author(1L, "Author_1");
        Genre genre = new Genre(1L, "Genre_1");
        Book book = new Book(1L, "BookTitle_1", author, List.of(genre));
        Commentary commentary = new Commentary(1L, "Commentary_1", book);

        when(bookService.findAll()).thenReturn(List.of(book));
        when(authorService.findAll()).thenReturn(List.of(author));
        when(genreService.findAll()).thenReturn(List.of(genre));

        when(commentaryService.findByBookId(anyLong())).thenReturn(List.of(commentary));
        when(commentaryService.findByBookIdWithoutFetch(anyLong())).thenReturn(List.of(commentary));

        when(bookService.findById(anyLong())).thenReturn(Optional.of(book));
        when(commentaryService.findById(anyLong())).thenReturn(Optional.of(commentary));
    }
}
