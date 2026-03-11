package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.klimachkov.hw.dao.QuestionDao;
import ru.klimachkov.hw.domain.Question;
import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final String ENTER_ANSWER_NUMBER_ERROR_MESSAGE = "Please enter a number in the range %d-%d";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        TestResult testResult = new TestResult(student);
        List<Question> questions = questionDao.findAll();

        int questionNumber = 1;
        for (Question question : questions) {
            if (question.answers().isEmpty()) {
                continue;
            }

            int enteredAnswerNumber = ioService.readIntForRangeWithPrompt(
                    1,
                    question.answers().size(),
                    getQuestionWithAnswers(question, questionNumber++),
                    ENTER_ANSWER_NUMBER_ERROR_MESSAGE.formatted(1, question.answers().size())
            );

            testResult.applyAnswer(question, question.answers().get(enteredAnswerNumber - 1).isCorrect());
        }

        return testResult;
    }

    private String getQuestionWithAnswers(Question question, int questionNumber) {
        StringBuilder questionWithAnswersBuilder = new StringBuilder();
        questionWithAnswersBuilder.append(questionNumber)
                .append(".")
                .append(question.text())
                .append("\n");

        IntStream.range(0, question.answers().size()).forEach(answerIndex ->
                questionWithAnswersBuilder.append("\t")
                        .append(answerIndex + 1)
                        .append(".")
                        .append(question.answers().get(answerIndex).text())
                        .append("\n")
        );

        return questionWithAnswersBuilder.toString();
    }
}
