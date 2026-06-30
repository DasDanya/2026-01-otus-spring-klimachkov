package ru.klimachkov.hw;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.repositories.AuthorRepository;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.CommentaryRepository;
import ru.klimachkov.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataFiller implements ApplicationRunner {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentaryRepository commentaryRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<Author> authors = List.of(
                new Author("Author_1"),
                new Author("Author_2"),
                new Author("Author_3")
        );

        List<Genre> genres = List.of(
                new Genre("Genre_1"), new Genre("Genre_2"),
                new Genre("Genre_3"), new Genre("Genre_4"),
                new Genre("Genre_5"), new Genre("Genre_6")
        );

        Flux.zip(authorRepository.saveAll(authors).collectList(), genreRepository.saveAll(genres).collectList())
                .flatMap(tuple -> saveBooks(tuple.getT1(), tuple.getT2()))
                .collectList()
                .flatMap(this::saveCommentaries)
                .subscribe();
    }

    private Flux<BookDto> saveBooks(List<Author> authors, List<Genre> genres) {
        List<BookDto> books = IntStream.range(1, 4)
            .mapToObj(i -> {
                BookDto book = new BookDto();
                book.setTitle("BookTitle_" + i);
                book.setAuthor(AuthorDto.of(authors.get(i - 1)));
                book.setGenres(genres.subList((i - 1) * 2, (i - 1) * 2 + 2).stream().map(GenreDto::of).toList());
                return book;
            }).toList();

        return Flux.fromIterable(books).flatMap(bookRepository::save);
    }

    private Mono<Void> saveCommentaries(List<BookDto> books) {
        List<CommentaryDto> commentaries = new ArrayList<>();
        for (BookDto book : books) {
            for (int i = 1; i < 4; i++) {
                CommentaryDto commentary = new CommentaryDto();
                commentary.setText("Commentary_" + (commentaries.size() + 1));
                commentary.setBook(book);
                commentaries.add(commentary);
            }
        }

        return Flux.fromIterable(commentaries).flatMap(commentaryRepository::save).then();
    }
}
