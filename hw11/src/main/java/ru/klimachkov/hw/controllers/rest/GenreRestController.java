package ru.klimachkov.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.services.GenreService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping
    public Flux<GenreDto> listGenres() {
        return genreService.findAll();
    }
}
