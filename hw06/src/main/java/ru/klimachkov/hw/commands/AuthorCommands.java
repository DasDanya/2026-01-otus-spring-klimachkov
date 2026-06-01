package ru.klimachkov.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.klimachkov.hw.converters.AuthorConverter;
import ru.klimachkov.hw.services.AuthorService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
