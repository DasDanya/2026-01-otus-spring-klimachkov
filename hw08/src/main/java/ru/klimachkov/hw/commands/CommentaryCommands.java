package ru.klimachkov.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.klimachkov.hw.converters.CommentaryConverter;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.services.CommentaryService;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommentaryCommands {

    private final CommentaryService commentaryService;

    private final CommentaryConverter commentaryConverter;

    @ShellMethod(value = "Find all commentaries by book id", key = "cbbid")
    public String findAllCommentariesByBookId(String bookId) {
        List<Commentary> commentaries = commentaryService.findByBookId(bookId);
        if (commentaries.isEmpty()) {
            return "Commentaries related to the book with id %s not found".formatted(bookId);
        }

        return commentaries.stream()
                .map(commentaryConverter::commentaryToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find commentary by id", key = "cbid")
    public String findCommentaryById(String id) {
        return commentaryService.findById(id)
                .map(commentaryConverter::commentaryToString)
                .orElse("Commentary with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert commentary", key = "cins")
    public String insertCommentary(String text, String bookId) {
        Commentary insertedCommentary = commentaryService.insert(text, bookId);
        return commentaryConverter.commentaryToString(insertedCommentary);
    }

    @ShellMethod(value = "Update commentary", key = "cupd")
    public String updateCommentary(String id, String text, String bookId) {
        Commentary updatedCommentary = commentaryService.update(id, text, bookId);
        return commentaryConverter.commentaryToString(updatedCommentary);
    }

    @ShellMethod(value = "Delete commentary by id", key = "cdel")
    public void deleteCommentary(String id) {
        commentaryService.deleteById(id);
    }
}
