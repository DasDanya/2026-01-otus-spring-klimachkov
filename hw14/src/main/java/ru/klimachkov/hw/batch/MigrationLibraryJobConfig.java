package ru.klimachkov.hw.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.transaction.PlatformTransactionManager;
import ru.klimachkov.hw.models.mongo.AuthorDocument;
import ru.klimachkov.hw.models.mongo.BookDocument;
import ru.klimachkov.hw.models.mongo.CommentaryDocument;
import ru.klimachkov.hw.models.mongo.GenreDocument;
import ru.klimachkov.hw.models.relational.Author;
import ru.klimachkov.hw.models.relational.Book;
import ru.klimachkov.hw.models.relational.Commentary;
import ru.klimachkov.hw.models.relational.Genre;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BatchProperties.class)
public class MigrationLibraryJobConfig {

    public static final String MIGRATE_LIBRARY_JOB_NAME = "migrateLibraryJob";

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final BatchProperties batchProperties;

    private final CleanupDecider cleanupDecider;

    @Bean
    public Step cleanupStep(MongoOperations mongoOperations, MigrationIdRegistry migrationIdRegistry) {
        return new StepBuilder("cleanupStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List.of(GenreDocument.class, AuthorDocument.class, BookDocument.class, CommentaryDocument.class)
                            .forEach(mongoOperations::dropCollection);
                    migrationIdRegistry.clear();
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step migrateAuthorsStep(
            ItemReader<Author> reader,
            ItemProcessor<Author, AuthorDocument> processor,
            ItemWriter<AuthorDocument> writer
    ) {
        return createStep("migrationAuthorsStep", reader, processor, writer);
    }

    @Bean
    public Step migrateGenresStep(
            ItemReader<Genre> reader,
            ItemProcessor<Genre, GenreDocument> processor,
            ItemWriter<GenreDocument> writer
    ) {
        return createStep("migrationGenresStep", reader, processor, writer);
    }

    @Bean
    public Step migrateBooksStep(
            ItemReader<Book> reader,
            ItemProcessor<Book, BookDocument> processor,
            ItemWriter<BookDocument> writer
    ) {
        return createStep("migrationBooksStep", reader, processor, writer);
    }

    @Bean
    public Step migrateCommentariesStep(
            ItemReader<Commentary> reader,
            ItemProcessor<Commentary, CommentaryDocument> processor,
            ItemWriter<CommentaryDocument> writer
    ) {
        return createStep("migrationCommentariesStep", reader, processor, writer);
    }

    @Bean
    public Job migrateLibraryJob(
            Step cleanupStep,
            Step migrateAuthorsStep,
            Step migrateGenresStep,
            Step migrateBooksStep,
            Step migrateCommentariesStep
    ) {
        Flow migrationFlow = new FlowBuilder<Flow>("migrateLibraryFlow")
                .start(migrateAuthorsStep)
                .next(migrateGenresStep)
                .next(migrateBooksStep)
                .next(migrateCommentariesStep)
                .build();

        return new JobBuilder(MIGRATE_LIBRARY_JOB_NAME, jobRepository)
                .start(cleanupDecider)
                    .on(CleanupDecider.Status.CLEANUP.name()).to(cleanupStep).next(migrationFlow)
                .from(cleanupDecider)
                    .on(CleanupDecider.Status.SKIP_CLEANUP.name()).to(migrationFlow)
                .end()
                .build();
    }

    private <I, O> Step createStep(
            String stepName,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) {
        return new StepBuilder(stepName, jobRepository)
                .<I, O>chunk(batchProperties.getChunkSize(), platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
