package ru.klimachkov.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.klimachkov.hw.models.Author;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private long id;

    private String fullName;

    public static AuthorDto of(@NonNull Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
