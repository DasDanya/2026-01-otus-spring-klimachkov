package ru.klimachkov.hw.mongock.changeunit;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ChangeUnit(order = "003", id = "BookChangeUnit", author = "Klimachkov", runAlways = true)
public class BookChangeUnit {

    private final MongoOperations mongoOperations;

    @Execution
    public void execute() {
        mongoOperations.remove(new Query(), Book.class);

        List<Author> authors = mongoOperations.findAll(Author.class);
        List<Genre> genres = mongoOperations.findAll(Genre.class);

        List<Book> books = IntStream.range(1, 4)
                .mapToObj(i -> {
                    Book book = new Book();
                    book.setTitle("BookTitle_" + i);
                    book.setAuthor(authors.get(i - 1));
                    book.setGenres(genres.subList((i - 1) * 2, (i - 1) * 2 + 2));
                    return book;
                })
                .toList();

        mongoOperations.insert(books, Book.class);
    }

    @RollbackExecution
    public void rollback() {
        mongoOperations.remove(new Query(), Book.class);
    }
}
