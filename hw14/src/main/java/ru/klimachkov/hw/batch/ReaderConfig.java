package ru.klimachkov.hw.batch;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.klimachkov.hw.models.relational.Author;
import ru.klimachkov.hw.models.relational.Book;
import ru.klimachkov.hw.models.relational.Commentary;
import ru.klimachkov.hw.models.relational.Genre;

@Configuration
public class ReaderConfig {

    @Bean
    public JpaCursorItemReader<Author> authorReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Author>()
                .name("authorReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Author a")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Genre> genreReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Genre>()
                .name("genreReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT g FROM Genre g")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Book> bookReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT b FROM Book b")
                .build();
    }

    @Bean
    public JpaCursorItemReader<Commentary> commentaryReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<Commentary>()
                .name("commentaryReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM Commentary c")
                .build();
    }
}
