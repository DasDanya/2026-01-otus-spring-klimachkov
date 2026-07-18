package ru.klimachkov.hw.batch.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.batch.MigrationIdRegistry;
import ru.klimachkov.hw.models.mongo.AuthorDocument;
import ru.klimachkov.hw.models.mongo.BookDocument;
import ru.klimachkov.hw.models.mongo.GenreDocument;
import ru.klimachkov.hw.models.relational.Book;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookItemProcessor implements ItemProcessor<Book, BookDocument> {

    private final MigrationIdRegistry migrationIdRegistry;

    @Override
    public BookDocument process(Book book) {
        String authorDocumentId = migrationIdRegistry.determineAuthor(book.getAuthor().getId());
        AuthorDocument authorDocument = new AuthorDocument(authorDocumentId, book.getAuthor().getFullName());

        List<GenreDocument> genresDocuments = book.getGenres().stream()
                .map(genre -> {
                    String genreDocumentId = migrationIdRegistry.determineGenre(genre.getId());
                    return new GenreDocument(genreDocumentId, genre.getName());
                })
                .toList();

        String bookDocumentId = migrationIdRegistry.registerBook(book.getId());

        return new BookDocument(bookDocumentId, book.getTitle(), authorDocument, genresDocuments);
    }
}
