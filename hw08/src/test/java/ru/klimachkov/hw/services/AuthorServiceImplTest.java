package ru.klimachkov.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.repositories.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами книг")
@DataMongoTest
@Import(AuthorServiceImpl.class)
public class AuthorServiceImplTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = TestDataGenerator.generateAuthors();
        authorRepository.saveAll(authors);
    }

    @DisplayName("Должен загрузить список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        List<Author> actualAuthors = authorService.findAll();
        assertThat(actualAuthors).containsExactlyElementsOf(authors);
    }
}
