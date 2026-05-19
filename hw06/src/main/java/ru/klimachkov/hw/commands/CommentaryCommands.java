package ru.klimachkov.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.klimachkov.hw.converters.CommentaryConverter;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.services.CommentaryService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommentaryCommands {

    private final CommentaryService commentaryService;

    private final CommentaryConverter commentaryConverter;

    @ShellMethod(value = "Find all commentaries by book id", key = "cbbid")
    public String findAllCommentariesByBookId(long bookId) {
        return commentaryService.findAllByBookId(bookId).stream()
                .map(commentaryConverter::commentaryToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find commentary by id", key = "cbid")
    public String findCommentaryById(long id) {
        return commentaryService.findById(id)
                .map(commentaryConverter::commentaryToString)
                .orElse("Commentary with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Insert commentary", key = "cins")
    public String insertCommentary(String text, long bookId) {
        Commentary insertedCommentary = commentaryService.insert(text, bookId);
        return commentaryConverter.commentaryToString(insertedCommentary);
    }

    @ShellMethod(value = "Update commentary", key = "cupd")
    public String updateCommentary(long id, String text, long bookId) {
        Commentary updatedCommentary = commentaryService.update(id, text, bookId);
        return commentaryConverter.commentaryToString(updatedCommentary);
    }

    @ShellMethod(value = "Delete commentary by id", key = "cdel")
    public void deleteCommentary(long id) {
        commentaryService.deleteById(id);
    }
}
