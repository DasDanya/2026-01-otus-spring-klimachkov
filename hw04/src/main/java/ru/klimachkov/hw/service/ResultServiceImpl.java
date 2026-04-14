package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.klimachkov.hw.config.LocaleConfig;
import ru.klimachkov.hw.config.TestConfig;
import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final LocaleConfig localeConfig;

    private final LocalizedIOService ioService;

    private final List<TestResult> testResults = new ArrayList<>();

    @Override
    public void save(TestResult testResult) {
        testResults.add(testResult);
    }

    @Override
    public void showResult(TestResult testResult) {
        ioService.printLine("");
        ioService.printLineLocalized("ResultService.test.results");
        ioService.printFormattedLineLocalized("ResultService.student",
                testResult.getStudent().getFullName());
        ioService.printFormattedLineLocalized("ResultService.finishedAt",
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)
                        .withLocale(localeConfig.getLocale())
                        .format(testResult.getFinishedAt())
        );
        ioService.printFormattedLineLocalized("ResultService.answered.questions.count",
                testResult.getAnsweredQuestions().size());
        ioService.printFormattedLineLocalized("ResultService.right.answers.count",
                testResult.getRightAnswersCount());

        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswersCountToPass()) {
            ioService.printLineLocalized("ResultService.passed.test");
            return;
        }

        ioService.printLineLocalized("ResultService.fail.test");
    }

    @Override
    public void showResults(Student student) {
        List<TestResult> studentResults = testResults.stream()
                .filter(testResult -> testResult.getStudent().equals(student))
                .sorted(Comparator.comparing(TestResult::getFinishedAt).reversed())
                .toList();

        if (studentResults.isEmpty()) {
            ioService.printFormattedLineLocalized(
                    "ResultService.student.test.results.not.found", student.getFullName());
        } else {
            studentResults.forEach(this::showResult);
        }
    }
}
