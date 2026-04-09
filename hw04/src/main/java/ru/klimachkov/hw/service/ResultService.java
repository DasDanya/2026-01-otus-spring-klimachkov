package ru.klimachkov.hw.service;

import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

public interface ResultService {

    void save(TestResult testResult);

    void showResult(TestResult testResult);

    default void saveAndShowResult(TestResult testResult) {
        save(testResult);
        showResult(testResult);
    }

    void showResults(Student student);
}
