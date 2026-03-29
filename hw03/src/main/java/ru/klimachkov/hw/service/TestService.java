package ru.klimachkov.hw.service;

import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

public interface TestService {
    TestResult executeTestFor(Student student);
}
