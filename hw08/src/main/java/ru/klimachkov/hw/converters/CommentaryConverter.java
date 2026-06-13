package ru.klimachkov.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.klimachkov.hw.models.Commentary;

@Component
@RequiredArgsConstructor
public class CommentaryConverter {

    private final BookConverter bookConverter;

    public String commentaryToString(Commentary commentary) {
        return "Id: %s, text: %s, book: {%s}".formatted(
                commentary.getId(), commentary.getText(), bookConverter.bookToString(commentary.getBook()));
    }
}
