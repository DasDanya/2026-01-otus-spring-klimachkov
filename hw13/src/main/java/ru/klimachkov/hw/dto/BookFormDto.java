package ru.klimachkov.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFormDto {

    @PositiveOrZero
    private long id;

    @NotBlank
    @Size(min = 1, max = 255)
    private String title;

    @NotNull
    private Long authorId;

    @NotEmpty
    private Set<Long> genresIds;

    public static BookFormDto of(Book book) {
        return new BookFormDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
    }
}
