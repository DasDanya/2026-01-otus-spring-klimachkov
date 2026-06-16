package ru.klimachkov.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.klimachkov.hw.models.Commentary;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentaryDto {

    private long id;

    private String text;

    public static CommentaryDto of(Commentary commentary) {
        return new CommentaryDto(commentary.getId(), commentary.getText());
    }
}
