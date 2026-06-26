package ru.klimachkov.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.klimachkov.hw.models.Commentary;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaryDto {

    private long id;

    private String text;

    private BookDto book;

    public CommentaryDto(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public static CommentaryDto of(@NonNull Commentary commentary) {
        return new CommentaryDto(commentary.getId(), commentary.getText(), BookDto.of(commentary.getBook()));
    }
}
