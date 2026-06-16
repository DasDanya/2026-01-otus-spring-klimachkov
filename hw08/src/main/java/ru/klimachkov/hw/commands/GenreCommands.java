package ru.klimachkov.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.klimachkov.hw.converters.GenreConverter;
import ru.klimachkov.hw.services.GenreService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class GenreCommands {

    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @ShellMethod(value = "Find all genres", key = "ag")
    public String findAllGenres() {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

}
