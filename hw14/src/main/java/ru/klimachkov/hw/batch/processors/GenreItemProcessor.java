package ru.klimachkov.hw.batch.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.batch.MigrationIdRegistry;
import ru.klimachkov.hw.models.mongo.GenreDocument;
import ru.klimachkov.hw.models.relational.Genre;

@Component
@RequiredArgsConstructor
public class GenreItemProcessor implements ItemProcessor<Genre, GenreDocument> {

    private final MigrationIdRegistry migrationIdRegistry;

    @Override
    public GenreDocument process(Genre genre) {
        return new GenreDocument(migrationIdRegistry.registerGenre(genre.getId()), genre.getName());
    }
}
