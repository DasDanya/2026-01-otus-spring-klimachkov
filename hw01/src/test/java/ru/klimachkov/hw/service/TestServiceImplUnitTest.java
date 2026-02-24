package ru.klimachkov.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.klimachkov.hw.dao.QuestionDao;
import ru.klimachkov.hw.domain.Answer;
import ru.klimachkov.hw.domain.Question;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplUnitTest {

    @Mock
    private QuestionDao questionDao;

    @Mock
    private IOService ioService;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    @DisplayName("Execute test without questions")
    void executeTestWithoutQuestions() {
        InOrder inOrder = inOrder(ioService, questionDao);

        testService.executeTest();

        inOrder.verify(questionDao).findAll();
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        inOrder.verify(ioService).printLine(argCaptor.capture());

        assertThat(argCaptor.getValue()).isEqualTo("Questions list is empty");
    }

    @ParameterizedTest
    @DisplayName("Execute test with questions")
    @MethodSource("getQuestions")
    void executeTestWithQuestions(
            List<Question> questions,
            String expectedTestTitle,
            List<String> expectedQuestions,
            List<Integer> expectedAnswerNumbers,
            List<String> expectedAnswerTexts
    ) {
        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(questionDao).findAll();

        ArgumentCaptor<String> titleTestCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService).printFormattedLine(titleTestCaptor.capture());

        ArgumentCaptor<String> questionTextsCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService, times(questions.size())).printLine(questionTextsCaptor.capture());

        ArgumentCaptor<String> answerFormatsCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> answerNumbersCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> answerTextsCaptor = ArgumentCaptor.forClass(String.class);

        long countAnswers = questions.stream().mapToLong(q -> q.answers().size()).sum();
        verify(ioService, times((int) countAnswers)).printFormattedLine(
                answerFormatsCaptor.capture(), answerNumbersCaptor.capture(), answerTextsCaptor.capture());

        assertThat(titleTestCaptor.getValue()).isEqualTo(expectedTestTitle);
        assertThat(questionTextsCaptor.getAllValues()).isEqualTo(expectedQuestions);
        assertThat(answerNumbersCaptor.getAllValues()).isEqualTo(expectedAnswerNumbers);
        assertThat(answerTextsCaptor.getAllValues()).isEqualTo(expectedAnswerTexts);
    }

    private static Stream<Arguments> getQuestions() {
        String firstQuestionText = "What is the capital city of France?";
        String london = "London";
        String berlin = "Berlin";
        String paris = "Paris";

        Question firstQuestion = new Question(
                firstQuestionText,
                List.of(new Answer(london, false),
                        new Answer(berlin, false),
                        new Answer(paris, true))
        );

        String secondQuestionText = "What is the largest ocean on Earth?";
        String pacificOcean = "Pacific Ocean";
        String indianOcean = "Indian Ocean";
        String atlanticOcean = "Atlantic Ocean";

        Question secondQuestion = new Question(
                secondQuestionText,
                List.of(new Answer(pacificOcean, true),
                        new Answer(indianOcean, false),
                        new Answer(atlanticOcean, false))
        );

        return Stream.of(
                Arguments.of(
                        Arrays.asList(firstQuestion, secondQuestion),
                        "Please answer the questions below%n",
                        List.of(firstQuestionText, secondQuestionText),
                        List.of(1,2,3,1,2,3),
                        List.of(london, berlin, paris, pacificOcean, indianOcean, atlanticOcean)
                )
        );
    }
}
