package ru.klimachkov.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.klimachkov.hw.models.Commentary;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentaryFormDto {

    @PositiveOrZero
    private long id;

    @NotBlank
    @Size(min = 1, max = 255)
    private String text;

    @NotNull
    private Long bookId;

    public static CommentaryFormDto of(Commentary commentary) {
        return new CommentaryFormDto(commentary.getId(), commentary.getText(), commentary.getBook().getId());
    }
}
