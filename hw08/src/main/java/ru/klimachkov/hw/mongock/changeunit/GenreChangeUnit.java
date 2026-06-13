package ru.klimachkov.hw.mongock.changeunit;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ChangeUnit(order = "002", id = "GenreChangeUnit", author = "Klimachkov", runAlways = true)
public class GenreChangeUnit {

    private final MongoOperations mongoOperations;

    @Execution
    public void execute() {
        mongoOperations.remove(new Query(), Genre.class);
        List<Genre> genres = IntStream.range(1, 7)
                .mapToObj(i -> new Genre(null, "Genre_" + i))
                .toList();

        mongoOperations.insert(genres, Genre.class);
    }

    @RollbackExecution
    public void rollback() {
        mongoOperations.remove(new Query(), Genre.class);
    }
}
