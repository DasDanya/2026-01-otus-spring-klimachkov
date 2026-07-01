package ru.klimachkov.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.BookGenre;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.List;

@TestComponent
@RequiredArgsConstructor
public class TestDataFactory {

    private final R2dbcEntityOperations entityOperations;

    public AuthorDto createAuthor(int number) {
        Author author = entityOperations.insert(new Author("Author_" + number))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to create author"));

        return AuthorDto.of(author);
    }

    @SuppressWarnings("ConstantConditions")
    public GenreDto createGenre(int number) {
        Genre genre = entityOperations.insert(new Genre("Genre_" + ((number - 1) * 2 + 1)))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to create genre"));

        return GenreDto.of(genre);
    }

    public BookDto createBook() {
        return createBook(1);
    }

    public BookDto createBook(int number) {
        AuthorDto author = createAuthor(number);

        GenreDto genre1 = createGenre((number - 1) * 2 + 1);
        GenreDto genre2 = createGenre((number - 1) * 2 + 2);

        Book book = new Book();
        book.setTitle("BookTitle_" + number);
        book.setAuthorId(author.getId());

        Book savedBook = entityOperations.insert(book)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to create book"));

        entityOperations.insert(new BookGenre(savedBook.getId(), genre1.getId()))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to create book-genre relation"));

        entityOperations.insert(new BookGenre(savedBook.getId(), genre2.getId()))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to create book-genre relation"));

        return new BookDto(
                savedBook.getId(),
                savedBook.getTitle(),
                author,
                List.of(genre1, genre2)
        );
    }

    public CommentaryDto createCommentary() {
        return createCommentary(createBook(1), 1);
    }

    public CommentaryDto createCommentary(BookDto book, int number) {
        Commentary commentary = new Commentary();
        commentary.setText("Commentary_" + number);
        commentary.setBookId(book.getId());

        Commentary savedCommentary = entityOperations.insert(commentary)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Failed to create commentary"));

        return new CommentaryDto(savedCommentary.getId(), savedCommentary.getText(), book);
    }
}
