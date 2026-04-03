package ru.klimachkov.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.klimachkov.hw.config.TestFileNameProvider;
import ru.klimachkov.hw.domain.Answer;
import ru.klimachkov.hw.domain.Question;
import ru.klimachkov.hw.exceptions.QuestionReadException;
import ru.klimachkov.hw.io.ReaderProvider;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider testFileNameProvider;

    @Mock
    private ReaderProvider readerProvider;

    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

    @Test
    @DisplayName("When resource not found then throw business exception")
    void whenResourceNotFound_thenThrowBusinessException() {
        when(testFileNameProvider.getTestFileName()).thenReturn("notExists.csv");
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }

    @Test
    @DisplayName("When resource is not .csv then throw business exception")
    void whenResourceIsNotCsv_thenThrowBusinessException() {
        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv1");
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }

    @Test
    @DisplayName("When reader is null then throw business exception")
    void whenReaderIsNull_thenThrowBusinessException() {
        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
        when(readerProvider.getReader(anyString())).thenReturn(null);
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }

    @Test
    @DisplayName("When .csv is empty then return empty question list")
    void whenCsvIsEmpty_thenReturnEmptyQuestionList() throws IOException {
        String resourceContent = "";

        when(testFileNameProvider.getTestFileName()).thenReturn("empty.csv");

        Reader spyReader = spy(new StringReader(resourceContent));
        when(readerProvider.getReader(anyString())).thenReturn(spyReader);
        assertThat(csvQuestionDao.findAll()).isEmpty();
        verify(spyReader).close();
    }

    @Test
    @DisplayName("Should dao skip first line")
    void shouldDaoSkipFirstLine() throws IOException {
        String resourceContent = """
                What is the largest ocean on Earth?;Pacific Ocean%true|Indian Ocean%false|Arctic Ocean%false
                What is the capital city of France?;London%false|Paris%true|Berlin%false""";

        when(testFileNameProvider.getTestFileName()).thenReturn("skipFirstLine.csv");

        Reader spyReader = spy(new StringReader(resourceContent));
        when(readerProvider.getReader(anyString())).thenReturn(spyReader);

        List<Question> questions = csvQuestionDao.findAll();
        assertThat(questions.size()).isOne();

        Question expectedQuestion = new Question(
                "What is the capital city of France?",
                List.of(new Answer("London", false),
                        new Answer("Paris", true),
                        new Answer("Berlin", false)
                )
        );
        assertThat(questions.get(0)).usingRecursiveAssertion().isEqualTo(expectedQuestion);
        verify(spyReader).close();
    }

    @Test
    @DisplayName("When .csv has wrong separator then answers not exists")
    void whenCsvHasWrongSeparator_thenAnswersNotExists() throws IOException {
        String resourceContent = """
                Skip line
                What is the largest ocean on Earth?^Pacific Ocean%true|Indian Ocean%false|Arctic Ocean%false
                What is the capital city of France?@London%false|Paris%true|Berlin%false""";

        when(testFileNameProvider.getTestFileName()).thenReturn("wrongSeparator.csv");

        Reader spyReader = spy(new StringReader(resourceContent));
        when(readerProvider.getReader(anyString())).thenReturn(spyReader);
        assertThat(csvQuestionDao.findAll().stream().allMatch(q -> q.answers() == null)).isTrue();
        verify(spyReader).close();
    }

    @Test
    @DisplayName("When .csv has wrong answer value separator then throw business exception")
    void whenCsvHasWrongAnswerValueSeparator_thenThrowBusinessException() throws IOException {
        String resourceContent = """
                Skip line
                What is the largest ocean on Earth?;Pacific Ocean^true|Indian Ocean%false|Arctic Ocean%false
                What is the capital city of France?;London%false|Paris%true|Berlin%false""";

        when(testFileNameProvider.getTestFileName()).thenReturn("wrongSeparator.csv");

        Reader spyReader = spy(new StringReader(resourceContent));
        when(readerProvider.getReader(anyString())).thenReturn(spyReader);
        assertThatThrownBy(() -> csvQuestionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasCause(new RuntimeException("java.lang.ArrayIndexOutOfBoundsException: " +
                        "Index 1 out of bounds for length 1"));

        verify(spyReader).close();
    }

    @Test
    @DisplayName("Should successfully read questions")
    void shouldSuccessfullyReadQuestions() throws IOException {
        String resourceContent = """
                Skip line
                What is the largest ocean on Earth?;Pacific Ocean%true|Indian Ocean%false|Arctic Ocean%false
                What is the capital city of France?;London%false|Paris%true|Berlin%false""";

        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");

        Reader spyReader = spy(new StringReader(resourceContent));
        when(readerProvider.getReader(anyString())).thenReturn(spyReader);

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
        verify(spyReader).close();
    }
}
