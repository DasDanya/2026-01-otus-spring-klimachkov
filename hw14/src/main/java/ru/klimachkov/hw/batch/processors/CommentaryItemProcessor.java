package ru.klimachkov.hw.batch.processors;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.batch.MigrationIdRegistry;
import ru.klimachkov.hw.models.mongo.BookDocument;
import ru.klimachkov.hw.models.mongo.CommentaryDocument;
import ru.klimachkov.hw.models.relational.Commentary;

@Component
@RequiredArgsConstructor
public class CommentaryItemProcessor implements ItemProcessor<Commentary, CommentaryDocument> {

    private final MigrationIdRegistry migrationIdRegistry;

    @Override
    public CommentaryDocument process(Commentary commentary) {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setId(migrationIdRegistry.determineBook(commentary.getBook().getId()));
        
        return new CommentaryDocument(new ObjectId().toString(), commentary.getText(), bookDocument);
    }
}
