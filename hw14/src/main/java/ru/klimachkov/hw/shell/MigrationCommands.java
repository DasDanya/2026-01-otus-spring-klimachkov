package ru.klimachkov.hw.shell;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.klimachkov.hw.batch.CleanupDecider;
import ru.klimachkov.hw.models.mongo.AuthorDocument;
import ru.klimachkov.hw.models.mongo.BookDocument;
import ru.klimachkov.hw.models.mongo.CommentaryDocument;
import ru.klimachkov.hw.models.mongo.GenreDocument;

@ShellComponent
@RequiredArgsConstructor
public class MigrationCommands {

    private final JobLauncher jobLauncher;

    private final Job migrationLibraryJob;

    private final EntityManager entityManager;

    private final MongoOperations mongoOperations;

    @ShellMethod(value = "Run migration from a relational database to MongoDB", key = "rm")
    public String runMigration() {
        return runMigration(false, false);
    }

    @ShellMethod(value = "Run migration from a relational database to MongoDB with pre-cleanup", key = "crm")
    public String runCleanedMigration() {
        return runMigration(false, true);
    }

    @ShellMethod(value = "Re-run migration from a relational database to MongoDB", key = "rrm")
    public String rerunMigration() {
        return runMigration(true, false);
    }

    @ShellMethod(value = "Re-run migration from a relational database to MongoDB with pre-cleanup", key = "rcrm")
    public String rerunCleanedMigration() {
        return runMigration(true, true);
    }

    @ShellMethod(value = "Show record counts in the relational database", key = "src")
    public String showRecordCountsInRelationalDb() {
        return """
                    authors: %d
                    genres:  %d
                    books:   %d
                    commentaries: %d
                """.formatted(
                (long) entityManager.createQuery("SELECT COUNT(a) FROM Author a").getSingleResult(),
                (long) entityManager.createQuery("SELECT COUNT(g) FROM Genre g").getSingleResult(),
                (long) entityManager.createQuery("SELECT COUNT(b) FROM Book b").getSingleResult(),
                (long) entityManager.createQuery("SELECT COUNT(c) FROM Commentary c").getSingleResult());
    }

    @ShellMethod(value = "Show document counts in the MongoDB", key = "sdc")
    public String showDocumentCountsInMongoDb() {
        return """
                    authors: %d
                    genres:  %d
                    books:   %d
                    commentaries: %d
                """.formatted(
                mongoOperations.count(new Query(), AuthorDocument.class),
                mongoOperations.count(new Query(), GenreDocument.class),
                mongoOperations.count(new Query(), BookDocument.class),
                mongoOperations.count(new Query(), CommentaryDocument.class));
    }

    private String runMigration(boolean allowRestart, boolean enableCleanup) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addString(CleanupDecider.CLEANUP_PARAMETER, String.valueOf(enableCleanup));

        if (allowRestart) {
            jobParametersBuilder.addLong("run.id", System.currentTimeMillis());
        }

        try {
            JobExecution jobExecution = jobLauncher.run(migrationLibraryJob, jobParametersBuilder.toJobParameters());
            return "Migration job finished: status=%s (cleanup=%s, allowRestart=%s, jobId=%d)".formatted(
                    jobExecution.getStatus(), enableCleanup, allowRestart, jobExecution.getJobId()
            );
        } catch (JobInstanceAlreadyCompleteException ex) {
            return "Migration job was already completed";
        } catch (JobExecutionAlreadyRunningException ex) {
            return "Migration job is already running";
        } catch (JobRestartException ex) {
            return "Migration job cannot be restarted";
        } catch (JobParametersInvalidException ex) {
            return "Invalid job parameters provided";
        }
    }
}
