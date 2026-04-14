package ru.klimachkov.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.klimachkov.hw.IgnoreShellTest;
import ru.klimachkov.hw.dao.QuestionDao;
import ru.klimachkov.hw.domain.Answer;
import ru.klimachkov.hw.domain.Question;
import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.domain.TestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TestServiceImplTest extends IgnoreShellTest {

    @MockitoBean
    private QuestionDao questionDao;

    @MockitoBean
    private LocalizedIOService localizedIOService;

    @Autowired
    private TestServiceImpl testService;

    @ParameterizedTest
    @MethodSource("getStudentArgument")
    @DisplayName("Test result should match student data")
    void testResultShouldMatchInputStudentData(Student student) {
        TestResult testResult = testService.executeTestFor(student);
        assertThat(student).usingRecursiveAssertion().isEqualTo(testResult.getStudent());
    }

    @ParameterizedTest
    @MethodSource("getStudentArgument")
    @DisplayName("Execute test without questions")
    void executeTestWithoutQuestions(Student student) {
        TestResult testResult = testService.executeTestFor(student);

        assertThat(testResult.getAnsweredQuestions()).isEmpty();
        assertThat(testResult.getRightAnswersCount()).isZero();
    }

    @ParameterizedTest
    @DisplayName("Execute test with questions")
    @MethodSource("getTestArguments")
    void executeTestWithQuestions(
            Student student,
            List<Question> questions
    ) {
        List<Integer> correctAnswerNumbers = new ArrayList<>();
        for (Question question : questions) {
            for (int i = 0; i < question.answers().size(); i++) {
                if (question.answers().get(i).isCorrect()) {
                    correctAnswerNumbers.add(i + 1);
                    break;
                }
            }
        }

        ArgumentCaptor<Integer> minRangeCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> maxRangeCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> promptTextCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> errorMessageCaptor = ArgumentCaptor.forClass(String.class);

        AtomicInteger readAnswersCounter = new AtomicInteger();
        when(localizedIOService.readIntForRangeWithPrompt(
                minRangeCaptor.capture(),
                maxRangeCaptor.capture(),
                promptTextCaptor.capture(),
                errorMessageCaptor.capture())
        ).thenAnswer(invocation -> correctAnswerNumbers.get(readAnswersCounter.getAndIncrement()));

        when(questionDao.findAll()).thenReturn(questions);

        TestResult testResult = testService.executeTestFor(student);

        assertThat(testResult.getAnsweredQuestions().size()).isEqualTo(readAnswersCounter.get());
        assertThat(testResult.getRightAnswersCount()).isEqualTo(correctAnswerNumbers.size());
    }

    private static Stream<Arguments> getStudentArgument() {
        return Stream.of(Arguments.of(getStudent()));
    }

    private static Student getStudent() {
        return new Student("Ivan", "Ivanov");
    }

    private static Stream<Arguments> getTestArguments() {
        List<Question> questions = List.of(
                createQuestion("Which option is a good way to handle the exception?",
                        2,
                        "@SneakyThrow", "e.printStackTrace()", "Rethrow with wrapping in business exception"),
                createQuestion(
                        "What is the capital city of France?",
                        2,
                        "London", "Berlin", "Paris"),
                createQuestion("What is the largest ocean on Earth?",
                        0,
                        "Pacific Ocean", "Indian Ocean", "Atlantic Ocean"),
                new Question("What happens if the question has no possible answers?", Collections.emptyList())

        );
        return Stream.of(Arguments.of(getStudent(), questions));
    }

    private static Question createQuestion(String text, int correctAnswerIndex, String... answers) {
        List<Answer> answersList = IntStream.range(0, answers.length)
                .mapToObj(i -> new Answer(answers[i], i == correctAnswerIndex))
                .toList();

        return new Question(text, answersList);
    }
}
