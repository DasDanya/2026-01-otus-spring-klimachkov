package ru.klimachkov.hw.batch.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.batch.MigrationIdRegistry;
import ru.klimachkov.hw.models.mongo.AuthorDocument;
import ru.klimachkov.hw.models.relational.Author;

@Component
@RequiredArgsConstructor
public class AuthorItemProcessor implements ItemProcessor<Author, AuthorDocument> {

    private final MigrationIdRegistry migrationIdRegistry;

    @Override
    public AuthorDocument process(Author author) {
        return new AuthorDocument(migrationIdRegistry.registerAuthor(author.getId()), author.getFullName());
    }
}
