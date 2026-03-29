package ru.klimachkov.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.config.TestFileNameProvider;
import ru.klimachkov.hw.dao.dto.QuestionDto;
import ru.klimachkov.hw.domain.Question;
import ru.klimachkov.hw.exceptions.QuestionReadException;
import ru.klimachkov.hw.io.ReaderProvider;

import java.io.Reader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private final TestFileNameProvider fileNameProvider;

    private final ReaderProvider readerProvider;

    @Override
    public List<Question> findAll() {
        try (Reader reader = readerProvider.getReader(fileNameProvider.getTestFileName())) {
            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build();

            return csvToBean.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (Exception e) {
            throw new QuestionReadException("Failed to read questions", e);
        }
    }
}
