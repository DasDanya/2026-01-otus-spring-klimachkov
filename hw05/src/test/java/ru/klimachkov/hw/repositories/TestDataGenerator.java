package ru.klimachkov.hw.repositories;

import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

public class TestDataGenerator {

    public static List<Author> generateAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    public static List<Genre> generateGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    public static List<Book> generateBooks() {
        List<Author> authors = generateAuthors();
        List<Genre> genres = generateGenres();

        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        authors.get(id - 1),
                        genres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }
}
