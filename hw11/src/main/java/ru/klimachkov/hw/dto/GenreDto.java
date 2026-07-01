package ru.klimachkov.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.klimachkov.hw.models.Genre;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {

    private long id;

    private String name;

    public static GenreDto of(@NonNull Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

}
