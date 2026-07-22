package ru.klimachkov.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.klimachkov.hw.TestDataCreator;
import ru.klimachkov.hw.models.Role;
import ru.klimachkov.hw.models.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с пользователями")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        List<Role> roles = TestDataCreator.createRoles();
        roles.forEach(entityManager::persist);

        user = TestDataCreator.createUser("user", roles);
        entityManager.persistAndFlush(user);
    }

    @DisplayName("Должен загрузить пользователя по имени")
    @Test
    void shouldReturnCorrectUserByUsername() {
        Optional<User> optionalActualUser = userRepository.findByUsername(user.getUsername());
        User expectedUser = entityManager.find(User.class, user.getId());

        assertThat(optionalActualUser).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }
}
