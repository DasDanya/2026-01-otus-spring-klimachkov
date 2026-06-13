package ru.klimachkov.hw;

import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestDataGenerator {

    public static List<Author> generateAuthors() {
        return IntStream.range(1, 4)
                .mapToObj(i -> {
                    Author author = new Author();
                    author.setFullName("Author_" + i);
                    return author;
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Genre> generateGenres() {
        return IntStream.range(1, 7)
                .mapToObj(i -> {
                    Genre genre = new Genre();
                    genre.setName("Genre_" + i);
                    return genre;
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Book> generateBooks(List<Author> authors, List<Genre> genres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> {
                    Book book = new Book();
                    book.setTitle("BookTitle_" + id);
                    book.setAuthor(authors.get(id - 1));
                    book.setGenres(genres.subList((id - 1) * 2, (id - 1) * 2 + 2));
                    return book;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Commentary> generateCommentaries(List<Book> books) {
        return IntStream.range(1, 10).boxed()
                .map(id -> {
                    Commentary commentary = new Commentary();
                    commentary.setText("Commentary_" + id);
                    commentary.setBook(books.get((id - 1) / 3));
                    return commentary;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
