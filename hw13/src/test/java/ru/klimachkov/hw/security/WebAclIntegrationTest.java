package ru.klimachkov.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тестирование эндпоинтов контроллеров с использованием ACL")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(value = "/data-acl.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class WebAclIntegrationTest {

    private static final Long BOOK_ID = 1L;

    private static final Long COMMENTARY_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Администратор должен успешно отредактировать комментарий")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldEditCommentaryWhenUserIsAdmin() throws Exception {
        expectSuccessfulEditCommentary();
    }

    @DisplayName("Пользователь, имеющий permission WRITE, должен успешно отредактировать комментарий")
    @Test
    @WithMockUser(username = "commentaryOwner", roles = "USER")
    void shouldEditCommentaryWhenUserHasWritePermission() throws Exception {
        expectSuccessfulEditCommentary();
    }

    @DisplayName("Пользователь, не имеющий permission WRITE, при попытке редактирования комментария " +
            "должен перенаправиться на станицу, отображающую 403")
    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void shouldNotEditCommentaryWhenUserNotHasWritePermission() throws Exception {
        expectSuccessfulForwardToErrorsForbidden(performEditCommentary());
    }

    @DisplayName("Администратор должен успешно удалить комментарий")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteCommentaryWhenUserIsAdmin() throws Exception {
        expectSuccessfulDeleteCommentary();
    }

    @DisplayName("Пользователь, имеющий permission DELETE, должен успешно удалить комментарий")
    @Test
    @WithMockUser(username = "commentaryOwner", roles = "USER")
    void shouldDeleteCommentaryWhenUserHasDeletePermission() throws Exception {
        expectSuccessfulDeleteCommentary();
    }

    @DisplayName("Пользователь, не имеющий permission DELETE, при попытке удаления комментария " +
            "должен перенаправиться на станицу, отображающую 403")
    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void shouldNotDeleteCommentaryWhenUserNotHasDeletePermission() throws Exception {
        expectSuccessfulForwardToErrorsForbidden(performDeleteCommentary());
    }

    private ResultActions performEditCommentary() throws Exception {
        return mockMvc.perform(post("/books/{bookId}/commentaries/{id}/edit", BOOK_ID, COMMENTARY_ID)
                .with(csrf())
                .formField("text", "UpdatedCommentary"));
    }

    private ResultActions performDeleteCommentary() throws Exception {
        return mockMvc.perform(post("/books/{bookId}/commentaries/{id}/delete", BOOK_ID, COMMENTARY_ID)
                .with(csrf()));
    }

    private void expectSuccessfulEditCommentary() throws Exception {
        expectSuccessfulRedirectToBookCommentaries(performEditCommentary());
    }

    private void expectSuccessfulDeleteCommentary() throws Exception {
        expectSuccessfulRedirectToBookCommentaries(performDeleteCommentary());
    }

    private void expectSuccessfulRedirectToBookCommentaries(ResultActions resultActions) throws Exception {
        resultActions.andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/books/{bookId}/commentaries", BOOK_ID));
    }

    private void expectSuccessfulForwardToErrorsForbidden(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(forwardedUrl("/errors/forbidden"));
    }
}
