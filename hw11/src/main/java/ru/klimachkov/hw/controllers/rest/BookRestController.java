package ru.klimachkov.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.BookFormDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.BookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public Flux<BookDto> listBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookDto> getBook(@PathVariable long id) {
        return bookService.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %d not found".formatted(id))));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> addBook(@Valid @RequestBody BookFormDto bookFormDto) {
        return bookService.insert(bookFormDto.getTitle(), bookFormDto.getAuthorId(), bookFormDto.getGenresIds());
    }

    @PutMapping("/{id}")
    public Mono<BookDto> editBook(@PathVariable long id, @Valid @RequestBody BookFormDto bookFormDto) {
        return bookService.update(id, bookFormDto.getTitle(), bookFormDto.getAuthorId(), bookFormDto.getGenresIds());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable long id) {
        return bookService.deleteById(id);
    }
}
