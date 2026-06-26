package ru.klimachkov.hw.controllers.rest;

import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.dto.GenreDto;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class JsonMatchers {

    public static Stream<ResultMatcher> getAuthorJsonMatchersStream(@NonNull AuthorDto author, String jsonPrefix) {
        return Stream.of(
                jsonPath("$%s.id".formatted(jsonPrefix), equalTo((int) author.getId())),
                jsonPath("$%s.fullName".formatted(jsonPrefix), equalTo(author.getFullName()))
        );
    }

    public static Stream<ResultMatcher> getGenreJsonMatchersStream(@NonNull GenreDto genre, String jsonPrefix) {
        return Stream.of(
                jsonPath("$%s.id".formatted(jsonPrefix), equalTo((int) genre.getId())),
                jsonPath("$%s.name".formatted(jsonPrefix), equalTo(genre.getName()))
        );
    }

    public static Stream<ResultMatcher> getBookJsonMatchersStream(
            @NonNull BookDto book,
            @NonNull String jsonPathPrefix
    ) {
        Stream<ResultMatcher> bookAuthorMatchers = Stream.of(
                jsonPath("$%s.id".formatted(jsonPathPrefix), equalTo((int) book.getId())),
                jsonPath("$%s.title".formatted(jsonPathPrefix), equalTo(book.getTitle())),
                jsonPath("$%s.author.id".formatted(jsonPathPrefix), equalTo((int) book.getAuthor().getId())),
                jsonPath("$%s.author.fullName".formatted(jsonPathPrefix), equalTo(book.getAuthor().getFullName()))
        );

        Stream<ResultMatcher> bookGenresMatchers = Stream.concat(
                Stream.of(jsonPath("$%s.genres".formatted(jsonPathPrefix), hasSize(book.getGenres().size()))),
                IntStream.range(0, book.getGenres().size())
                        .mapToObj(i -> {
                            GenreDto genre = book.getGenres().get(i);
                            return Stream.of(
                                    jsonPath("$%s.genres[%d].id".formatted(jsonPathPrefix, i),
                                            equalTo((int) genre.getId())),
                                    jsonPath("$%s.genres[%d].name".formatted(jsonPathPrefix, i),
                                            equalTo(genre.getName()))
                            );
                        })
                        .flatMap(Function.identity())
        );

        return Stream.concat(bookAuthorMatchers, bookGenresMatchers);
    }

    public static Stream<ResultMatcher> getCommentaryJsonMatchersStream(
            @NonNull CommentaryDto commentary,
            @NonNull String jsonPathPrefix,
            boolean addBookMatchers
    ) {
        Stream<ResultMatcher> commentaryMatchers = Stream.of(
                jsonPath("$%s.id".formatted(jsonPathPrefix), equalTo((int) commentary.getId())),
                jsonPath("$%s.text".formatted(jsonPathPrefix), equalTo(commentary.getText()))
        );

        return addBookMatchers
                ? Stream.concat(
                    commentaryMatchers,
                    getBookJsonMatchersStream(commentary.getBook(), jsonPathPrefix + ".book"))
                : commentaryMatchers;
    }

}
