package ru.klimachkov.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.services.AuthorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public Flux<AuthorDto> listAuthors() {
        return authorService.findAll();
    }
}
