package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    @Override
    public void run() {
        Student student = studentService.determineCurrentStudent();
        TestResult testResult = testService.executeTestFor(student);
        resultService.showResult(testResult);
    }
}
