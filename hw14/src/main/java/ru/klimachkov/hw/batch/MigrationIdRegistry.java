package ru.klimachkov.hw.batch;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.batch.exceptions.MigratedIdNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MigrationIdRegistry {

    private final Map<Long, String> authorsIds = new ConcurrentHashMap<>();

    private final Map<Long, String> genresIds = new ConcurrentHashMap<>();

    private final Map<Long, String> booksIds = new ConcurrentHashMap<>();

    public void clear() {
        authorsIds.clear();
        genresIds.clear();
        booksIds.clear();
    }

    public String registerAuthor(long authorId) {
        return registerId(authorsIds, authorId);
    }

    public String registerGenre(long genreId) {
        return registerId(genresIds, genreId);
    }

    public String registerBook(long bookId) {
        return registerId(booksIds, bookId);
    }

    public String determineAuthor(long authorId) {
        return determineId(authorsIds, authorId, "Author");
    }

    public String determineGenre(long genreId) {
        return determineId(genresIds, genreId, "Genre");
    }

    public String determineBook(long bookId) {
        return determineId(booksIds, bookId, "Book");
    }

    private String registerId(Map<Long, String> storage, long sourceId) {
        String targetId = new ObjectId().toString();
        storage.put(sourceId, targetId);
        return targetId;
    }

    private String determineId(Map<Long,String> storage, long sourceId, String entityType) {
        String targetId = storage.get(sourceId);
        if (targetId == null) {
            throw new MigratedIdNotFoundException(
                    "%s migrated ID not found for source id %d".formatted(entityType, sourceId)
            );
        }

        return targetId;
    }
}
