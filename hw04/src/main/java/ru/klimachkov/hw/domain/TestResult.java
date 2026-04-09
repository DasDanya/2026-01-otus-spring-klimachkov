package ru.klimachkov.hw.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TestResult {
    private final Student student;

    private final List<Question> answeredQuestions;

    private LocalDateTime finishedAt;

    private int rightAnswersCount;

    public TestResult(Student student) {
        this.student = student;
        this.finishedAt = LocalDateTime.now();
        this.answeredQuestions = new ArrayList<>();
    }

    public void applyAnswer(Question question, boolean isRightAnswer) {
        answeredQuestions.add(question);
        if (isRightAnswer) {
            rightAnswersCount++;
        }
    }
}
