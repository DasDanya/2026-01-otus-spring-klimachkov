package ru.klimachkov.hw.mongock.changeunit;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ChangeUnit(order = "004", id = "CommentaryChangeUnit", author = "Klimachkov", runAlways = true)
public class CommentaryChangeUnit {

    private final MongoOperations mongoOperations;

    @Execution
    public void execute() {
        mongoOperations.remove(new Query(), Commentary.class);

        List<Book> books = mongoOperations.findAll(Book.class);

        List<Commentary> commentaries = IntStream.range(1, 10)
                .mapToObj(i -> {
                    Commentary commentary = new Commentary();
                    commentary.setText("Commentary_" + i);
                    commentary.setBook(books.get((i - 1) / 3));
                    return commentary;
                })
                .toList();

        mongoOperations.insert(commentaries, Commentary.class);
    }

    @RollbackExecution
    public void rollback() {
        mongoOperations.remove(new Query(), Commentary.class);
    }
}
