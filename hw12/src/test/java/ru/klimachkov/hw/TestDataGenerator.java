package ru.klimachkov.hw;

import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.models.Role;
import ru.klimachkov.hw.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestDataGenerator {

    public static List<Author> generateAuthors() {
        return generateAuthors(false);
    }

    public static List<Author> generateAuthors(boolean setId) {
        return IntStream.range(1, 4)
                .mapToObj(i -> {
                    Author author = new Author();
                    if (setId) {
                        author.setId(i);
                    }
                    author.setFullName("Author_" + i);
                    return author;
                }).collect(Collectors.toCollection(ArrayList::new));
    }


    public static List<Genre> generateGenres() {
        return generateGenres(false);
    }

    public static List<Genre> generateGenres(boolean setId) {
        return IntStream.range(1, 7)
                .mapToObj(i -> {
                    Genre genre = new Genre();
                    if (setId) {
                        genre.setId(i);
                    }
                    genre.setName("Genre_" + i);
                    return genre;
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Book> generateBooks(List<Author> authors, List<Genre> genres) {
        return generateBooks(authors, genres, false);
    }

    public static List<Book> generateBooks(List<Author> authors, List<Genre> genres, boolean setId) {
        return IntStream.range(1, 4).boxed()
                .map(id -> {
                    Book book = new Book();
                    if (setId) {
                        book.setId(id);
                    }
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

    public static List<Role> generateRoles() {
        return Stream.of("USER, ADMIN")
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                }).toList();
    }

    public static User generateUser(List<Role> roles) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoles(new HashSet<>(roles));

        return admin;
    }
}
