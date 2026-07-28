package ru.klimachkov.hw.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.klimachkov.hw.models.mongo.AuthorDocument;
import ru.klimachkov.hw.models.mongo.BookDocument;
import ru.klimachkov.hw.models.mongo.CommentaryDocument;
import ru.klimachkov.hw.models.mongo.GenreDocument;

@Configuration
public class WriterConfig {

    @Bean
    public ItemWriter<AuthorDocument> authorWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<AuthorDocument>()
                .template(mongoOperations)
                .collection("authors")
                .build();
    }

    @Bean
    public ItemWriter<GenreDocument> genreWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<GenreDocument>()
                .template(mongoOperations)
                .collection("genres")
                .build();
    }

    @Bean
    public ItemWriter<BookDocument> bookWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<BookDocument>()
                .template(mongoOperations)
                .collection("books")
                .build();
    }

    @Bean
    public ItemWriter<CommentaryDocument> commentaryWriter(MongoOperations mongoOperations) {
        return new MongoItemWriterBuilder<CommentaryDocument>()
                .template(mongoOperations)
                .collection("commentaries")
                .build();
    }
}
