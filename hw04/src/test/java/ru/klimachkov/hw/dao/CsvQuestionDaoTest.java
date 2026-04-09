package ru.klimachkov.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.klimachkov.hw.IgnoreShellTest;
import ru.klimachkov.hw.config.TestFileNameProvider;
import ru.klimachkov.hw.domain.Answer;
import ru.klimachkov.hw.domain.Question;
import ru.klimachkov.hw.exceptions.QuestionReadException;
import ru.klimachkov.hw.io.ReaderProvider;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest extends IgnoreShellTest {

    @MockitoSpyBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private ReaderProvider readerProvider;

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @Test
    @DisplayName("When resource not found then throw business exception")
    void whenResourceNotFound_thenThrowBusinessException() {
        when(testFileNameProvider.getTestFileName()).thenReturn("notExists.csv");
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }

    @Test
    @DisplayName("When .csv is empty then return empty question list")
    void whenCsvIsEmpty_thenReturnEmptyQuestionList() {
        when(testFileNameProvider.getTestFileName()).thenReturn("empty.csv");
        assertThat(csvQuestionDao.findAll()).isEmpty();
    }

    @Test
    @DisplayName("When .csv has wrong separator then answers not exists")
    void whenCsvHasWrongSeparator_thenAnswersNotExists() {
        when(testFileNameProvider.getTestFileName()).thenReturn("wrongSeparator.csv");
        assertThat(csvQuestionDao.findAll().stream().allMatch(q -> q.answers() == null)).isTrue();
    }

    @Test
    @DisplayName("When .csv has wrong answer value separator then throw business exception")
    void whenCsvHasWrongAnswerValueSeparator_thenThrowBusinessException() {
        when(testFileNameProvider.getTestFileName()).thenReturn("wrongValueSeparator.csv");
        assertThatThrownBy(() -> csvQuestionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasCause(new RuntimeException("java.lang.ArrayIndexOutOfBoundsException: " +
                        "Index 1 out of bounds for length 1"));
    }

    @Test
    @DisplayName("Should successfully read questions")
    void shouldSuccessfullyReadQuestions() {
        when(testFileNameProvider.getTestFileName()).thenReturn("successful.csv");
        List<Question> actualQuestions = csvQuestionDao.findAll();

        List<Question> expectedQuestions =
                List.of(
                        new Question("What is the largest ocean on Earth?",
                                List.of(new Answer("Pacific Ocean", true),
                                        new Answer("Indian Ocean", false),
                                        new Answer("Arctic Ocean", false))
                        ),
                        new Question("What is the capital city of France?",
                                List.of(new Answer("London", false),
                                        new Answer("Paris", true),
                                        new Answer("Berlin", false))
                        )
                );

        assertThat(actualQuestions).usingRecursiveAssertion().isEqualTo(expectedQuestions);
    }
}