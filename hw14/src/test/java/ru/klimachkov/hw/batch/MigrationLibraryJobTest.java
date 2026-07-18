package ru.klimachkov.hw.batch;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import ru.klimachkov.hw.models.mongo.AuthorDocument;
import ru.klimachkov.hw.models.mongo.BookDocument;
import ru.klimachkov.hw.models.mongo.CommentaryDocument;
import ru.klimachkov.hw.models.mongo.GenreDocument;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@SpringBatchTest
public class MigrationLibraryJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private MongoOperations mongoOperations;

    @DisplayName("Должен мигрировать все сущности и сохранить связи между ними")
    @Test
    void shouldMigrateAllEntitiesAndPreserveRelations() throws Exception {
        Job migrationJob = jobLauncherTestUtils.getJob();

        assertThat(migrationJob).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(MigrationLibraryJobConfig.MIGRATE_LIBRARY_JOB_NAME);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        assertThat(mongoOperations.count(new Query(), AuthorDocument.class)).isEqualTo(3);
        assertThat(mongoOperations.count(new Query(), GenreDocument.class)).isEqualTo(6);
        assertThat(mongoOperations.count(new Query(), BookDocument.class)).isEqualTo(3);
        assertThat(mongoOperations.count(new Query(), CommentaryDocument.class)).isEqualTo(9);

        assertPreserveRelations(
                "BookTitle_1", "Author_1",
                List.of("Genre_1", "Genre_2"), List.of("Commentary_1", "Commentary_2", "Commentary_3")
        );

        assertPreserveRelations(
                "BookTitle_2", "Author_2",
                List.of("Genre_3", "Genre_4"), List.of("Commentary_4", "Commentary_5", "Commentary_6")
        );
        assertPreserveRelations(
                "BookTitle_3", "Author_3",
                List.of("Genre_5", "Genre_6"), List.of("Commentary_7", "Commentary_8", "Commentary_9")
        );
    }

    private void assertPreserveRelations(
            String bookTitle,
            String authorFullName,
            List<String> genresNames,
            List<String> commentariesTexts
    ) {
        BookDocument bookDocument = mongoOperations.findOne(
                query(where("title").is(bookTitle)), BookDocument.class);

        assertThat(bookDocument).isNotNull();
        assertThat(ObjectId.isValid(bookDocument.getId())).isTrue();

        assertThat(bookDocument.getAuthor()).isNotNull();
        assertThat(ObjectId.isValid(bookDocument.getAuthor().getId())).isTrue();
        assertThat(bookDocument.getAuthor().getFullName()).isEqualTo(authorFullName);

        assertThat(bookDocument.getGenres())
                .isNotEmpty()
                .allSatisfy(genre -> {
                    assertThat(genre).isNotNull();
                    assertThat(ObjectId.isValid(genre.getId())).isTrue();
                    assertThat(genre.getName()).isIn(genresNames);
                });

        List<CommentaryDocument> commentariesDocuments =
                mongoOperations.find(query(where("book.id").is(bookDocument.getId())), CommentaryDocument.class);

        assertThat(commentariesDocuments)
                .isNotEmpty()
                .allSatisfy(commentary -> {
                    assertThat(commentary).isNotNull();
                    assertThat(ObjectId.isValid(commentary.getId())).isTrue();
                    assertThat(commentary.getText()).isIn(commentariesTexts);
                });
    }

}
