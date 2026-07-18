package ru.klimachkov.hw.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CleanupDecider implements JobExecutionDecider {

    public static final String CLEANUP_PARAMETER = "cleanup";

    @NonNull
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        boolean cleanup = Boolean.parseBoolean(jobExecution.getJobParameters().getString(CLEANUP_PARAMETER));
        return new FlowExecutionStatus(cleanup ? Status.CLEANUP.name() : Status.SKIP_CLEANUP.name());
    }

    public enum Status {
        CLEANUP,
        SKIP_CLEANUP
    }
}
