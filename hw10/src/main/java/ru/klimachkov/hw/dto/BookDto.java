package ru.klimachkov.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.klimachkov.hw.models.Book;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private long id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;

    public static BookDto of(@NonNull Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                AuthorDto.of(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::of).toList()
        );
    }
}
