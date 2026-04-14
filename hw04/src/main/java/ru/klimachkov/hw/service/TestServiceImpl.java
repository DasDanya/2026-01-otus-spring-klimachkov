package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.klimachkov.hw.dao.QuestionDao;
import ru.klimachkov.hw.domain.Question;
import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService localizedIOService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        localizedIOService.printLine("");
        localizedIOService.printLineLocalized("TestService.answer.the.questions");

        TestResult testResult = new TestResult(student);
        int questionNumber = 1;
        for (Question question : questionDao.findAll()) {
            if (question.answers().isEmpty()) {
                continue;
            }

            int enteredAnswerNumber = localizedIOService.readIntForRangeWithPrompt(
                    1,
                    question.answers().size(),
                    getQuestionWithAnswers(question, questionNumber++),
                    localizedIOService.getMessage(
                            "TestService.enter.answer.number.error.message", 1, question.answers().size())
            );

            testResult.applyAnswer(question, question.answers().get(enteredAnswerNumber - 1).isCorrect());
        }

        return testResult;
    }

    private String getQuestionWithAnswers(Question question, int questionNumber) {
        StringBuilder questionWithAnswersBuilder = new StringBuilder(
                "%d. %s\n".formatted(questionNumber, question.text())
        );

        IntStream.range(0, question.answers().size()).forEach(answerIndex ->
                questionWithAnswersBuilder.append(
                        "\t%d. %s\n".formatted(answerIndex + 1, question.answers().get(answerIndex).text())
                )
        );

        return questionWithAnswersBuilder.toString();
    }
}
