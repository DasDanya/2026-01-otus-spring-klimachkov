package ru.klimachkov.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
}
