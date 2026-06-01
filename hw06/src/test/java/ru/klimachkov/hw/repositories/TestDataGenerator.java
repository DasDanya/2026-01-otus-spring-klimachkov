package ru.klimachkov.hw.repositories;

import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

public class TestDataGenerator {

    public static List<Author> generateAuthors() {
        return IntStream.range(1, 4)
                .mapToObj(i -> {
                    Author author = new Author();
                    author.setFullName("Author_" + i);
                    return author;
                }).toList();
    }

    public static List<Genre> generateGenres() {
        return IntStream.range(1, 7)
                .mapToObj(i -> {
                    Genre genre = new Genre();
                    genre.setName("Genre_" + i);
                    return genre;
                }).toList();
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
                .toList();
    }

    public static List<Commentary> generateCommentaries(List<Book> books) {
        return IntStream.range(1, 10).boxed()
                .map(id -> {
                    Commentary commentary = new Commentary();
                    commentary.setText("Commentary_" + id);
                    commentary.setBook(books.get((id - 1) / 3));
                    return commentary;
                })
                .toList();
    }
}
