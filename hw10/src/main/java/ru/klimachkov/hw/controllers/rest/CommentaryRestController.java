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
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.dto.CommentaryFormDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.services.CommentaryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/{bookId}/commentaries")
public class CommentaryRestController {

    private final CommentaryService commentaryService;

    @GetMapping
    public List<CommentaryDto> listCommentaries(@PathVariable("bookId") long bookId) {
        return commentaryService.findByBookId(bookId);
    }

    @GetMapping("/only")
    public List<CommentaryDto> listCommentariesOnly(@PathVariable("bookId") long bookId) {
        return commentaryService.findByBookIdWithoutFetch(bookId);
    }

    @GetMapping("/{id}")
    public CommentaryDto getCommentary(@PathVariable("id") long id) {
        return commentaryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentary with id %d not found".formatted(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentaryDto addCommentary(
            @PathVariable("bookId") long bookId,
            @Valid @RequestBody CommentaryFormDto commentaryFormDto
    ) {
        return commentaryService.insert(commentaryFormDto.getText(), bookId);
    }

    @PutMapping("/{id}")
    public CommentaryDto editCommentary(
            @PathVariable("bookId") long bookId,
            @PathVariable("id") long id,
            @Valid @RequestBody CommentaryFormDto commentaryFormDto
    ) {
        return commentaryService.update(id, commentaryFormDto.getText(), bookId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentary(@PathVariable("id") long id) {
        commentaryService.deleteById(id);
    }
}
