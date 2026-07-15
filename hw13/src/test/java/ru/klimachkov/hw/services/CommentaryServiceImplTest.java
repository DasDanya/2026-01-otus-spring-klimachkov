package ru.klimachkov.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.klimachkov.hw.TestDataCreator;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.models.Role;
import ru.klimachkov.hw.models.User;
import ru.klimachkov.hw.security.acl.AclConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Сервис для работы с комментариями книг")
@DataJpaTest
@Import({CommentaryServiceImpl.class, AclConfig.class})
public class CommentaryServiceImplTest {

    private static final String COMMENTARY_OWNER = "commentaryOwner";

    @Autowired
    private CommentaryServiceImpl commentaryService;

    @Autowired
    private MutableAclService aclService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private List<Book> books;

    private List<Commentary> commentaries;

    private Role userRole;

    private User commentaryOwner;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(action -> {
            List<Author> authors = TestDataCreator.createAuthors();
            authors.forEach(entityManager::persist);

            List<Genre> genres = TestDataCreator.createGenres();
            genres.forEach(entityManager::persist);

            books = TestDataCreator.createBooks(authors, genres);
            books.forEach(entityManager::persist);

            commentaries = TestDataCreator.createCommentaries(books);
            commentaries.forEach(entityManager::persist);

            userRole = new Role();
            userRole.setName("USER");
            entityManager.persist(userRole);

            commentaryOwner = TestDataCreator.createUser(COMMENTARY_OWNER, Collections.singletonList(userRole));
            entityManager.persist(commentaryOwner);

            createAclFor(commentaryOwner, commentaries.get(0));

            entityManager.flush();

            return null;
        });
    }

    @AfterEach
    public void cleanUp() {
        transactionTemplate.execute(action -> {
            aclService.deleteAcl(new ObjectIdentityImpl(Commentary.class, commentaries.get(0).getId()), true);

            entityManager.remove(entityManager.merge(commentaryOwner));
            entityManager.remove(entityManager.merge(userRole));

            entityManager.flush();
            entityManager.clear();

            return null;
        });
    }

    @DisplayName("Должен загрузить список всех комментариев по id книги")
    @Test
    @WithMockUser
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnCommentariesListByBookId() {
        long targetBookId = books.get(0).getId();
        commentaryService.findByBookId(targetBookId)
                .forEach(this::assertDoesNotThrowLazyInitializationException);
    }

    @DisplayName("Должен загрузить комментарий по id")
    @Test
    @WithMockUser
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldReturnCommentaryById() {
        long targetId = commentaries.get(0).getId();

        Optional<Commentary> actualCommentary = commentaryService.findById(targetId);

        assertThat(actualCommentary).isPresent();
        assertDoesNotThrowLazyInitializationException(actualCommentary.get());
    }

    @DisplayName("Должен сохранить новый комментарий")
    @Test
    @WithMockUser
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveNewCommentary() {
        Commentary insertedCommentary = commentaryService.insert("New_Commentary", books.get(0).getId());
        assertThat(insertedCommentary).isNotNull().matches(commentary -> commentary.getId() > 0);
        assertDoesNotThrowLazyInitializationException(insertedCommentary);
    }

    @DisplayName("Администратор должен сохранить измененный комментарий")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveUpdatedCommentaryWhenUserIsAdmin() {
        updateCommentarySuccessful();
    }

    @DisplayName("Пользователь, имеющий permission WRITE, должен сохранить измененный комментарий")
    @Test
    @WithMockUser(username = COMMENTARY_OWNER, roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldSaveUpdatedCommentaryWhenUserHasWritePermission() {
        updateCommentarySuccessful();
    }

    @DisplayName("Пользователь, не имеющий permission WRITE, не должен сохранить измененный комментарий")
    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotSaveUpdatedCommentaryWhenUserNotHasWritePermission() {
        assertThatThrownBy(() -> commentaryService.update(
                commentaries.get(0).getId(), "Updated_Commentary", books.get(books.size() - 1).getId())
        ).isInstanceOf(AuthorizationDeniedException.class);
    }

    @DisplayName("Администратор должен удалить комментарий")
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldDeleteCommentaryWhenUserIsAdmin() {
        deleteCommentarySuccessful();
    }

    @DisplayName("Пользователь, имеющий permission DELETE, должен удалить комментарий")
    @Test
    @WithMockUser(username = COMMENTARY_OWNER, roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldDeleteCommentaryWhenUserHasDeletePermission() {
        deleteCommentarySuccessful();
    }

    @DisplayName("Пользователь, не имеющий permission WRITE, не должен удалить комментарий")
    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotDeleteCommentaryWhenUserNotHasDeletePermission() {
        assertThatThrownBy(() -> commentaryService.deleteById(commentaries.get(0).getId()))
                .isInstanceOf(AuthorizationDeniedException.class);
    }

    private void createAclFor(User commentaryOwner, Commentary commentary) {
        ObjectIdentity commentaryObjectIdentity = new ObjectIdentityImpl(Commentary.class, commentary.getId());
        Sid commentaryOwnerSid = new PrincipalSid(commentaryOwner.getUsername());

        MutableAcl acl = aclService.createAcl(commentaryObjectIdentity);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, commentaryOwnerSid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, commentaryOwnerSid, true);

        aclService.updateAcl(acl);
    }

    private void updateCommentarySuccessful() {
        Commentary updatedCommentary = commentaryService.update(
                commentaries.get(0).getId(), "Updated_Commentary", books.get(books.size() - 1).getId());

        assertThat(updatedCommentary).isNotNull();
        assertDoesNotThrowLazyInitializationException(updatedCommentary);
    }

    private void deleteCommentarySuccessful() {
        assertThatCode(() -> commentaryService.deleteById(commentaries.get(0).getId())).doesNotThrowAnyException();
    }

    private void assertDoesNotThrowLazyInitializationException(@NonNull Commentary commentary) {
        assertNotNull(commentary.getBook());
        assertDoesNotThrow(() -> commentary.getBook().getTitle());

        assertNotNull(commentary.getBook().getAuthor());
        assertDoesNotThrow(() -> commentary.getBook().getAuthor().getFullName());

        assertThat(commentary.getBook().getGenres()).isNotEmpty();
        assertDoesNotThrow(() -> commentary.getBook().getGenres().size());
    }
}
