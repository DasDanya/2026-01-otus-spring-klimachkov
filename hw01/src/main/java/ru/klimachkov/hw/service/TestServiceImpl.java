package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;
import ru.klimachkov.hw.dao.QuestionDao;
import ru.klimachkov.hw.domain.Question;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        List<Question> questions = questionDao.findAll();
        if (questions.isEmpty()) {
            ioService.printLine("Questions list is empty");
            return;
        }

        ioService.printFormattedLine("Please answer the questions below%n");
        questions.forEach(question -> {
            ioService.printLine(question.text());
            IntStream.range(0, question.answers().size())
                    .forEach(i ->
                            ioService.printFormattedLine("\t%d.%s", (i + 1), question.answers().get(i).text())
                    );
        });
    }
}
