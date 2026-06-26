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
import ru.klimachkov.hw.dto.BookDto;
import ru.klimachkov.hw.dto.BookFormDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> listBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable long id) {
        return bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto addBook(@Valid @RequestBody BookFormDto bookFormDto) {
        return bookService.insert(bookFormDto.getTitle(), bookFormDto.getAuthorId(), bookFormDto.getGenresIds());
    }

    @PutMapping("/{id}")
    public BookDto editBook(@PathVariable long id, @Valid @RequestBody BookFormDto bookFormDto) {
        return bookService.update(id, bookFormDto.getTitle(), bookFormDto.getAuthorId(), bookFormDto.getGenresIds());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
    }
}
