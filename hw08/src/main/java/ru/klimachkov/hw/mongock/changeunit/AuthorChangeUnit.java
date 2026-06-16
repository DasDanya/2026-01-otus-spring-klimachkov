package ru.klimachkov.hw.mongock.changeunit;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import ru.klimachkov.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ChangeUnit(order = "001", id = "AuthorChangeUnit", author = "Klimachkov", runAlways = true)
public class AuthorChangeUnit {

    private final MongoOperations mongoOperations;

    @Execution
    public void execute() {
        mongoOperations.remove(new Query(), Author.class);
        List<Author> authors = IntStream.range(1, 4)
                .mapToObj(i -> new Author(null, "Author_" + i))
                .toList();

        mongoOperations.insert(authors, Author.class);
    }

    @RollbackExecution
    public void rollback() {
        mongoOperations.remove(new Query(), Author.class);
    }
}
