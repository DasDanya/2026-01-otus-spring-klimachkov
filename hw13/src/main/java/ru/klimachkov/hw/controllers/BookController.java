package ru.klimachkov.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.klimachkov.hw.dto.BookFormDto;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.services.AuthorService;
import ru.klimachkov.hw.services.BookService;
import ru.klimachkov.hw.services.CommentaryService;
import ru.klimachkov.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentaryService commentaryService;

    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        return "books/list";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));

        List<CommentaryDto> commentaries = commentaryService.findByBookIdWithoutFetch(id)
                .stream()
                .map(CommentaryDto::of)
                .toList();

        model.addAttribute("book", book);
        model.addAttribute("commentaries", commentaries);

        return "books/view";
    }

    @GetMapping("/add")
    public String addBook(Model model) {
        populateModelWithAuthorsAndGenres(model);
        model.addAttribute("book", new BookFormDto());
        return "books/edit";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") BookFormDto bookFormDto,
                          BindingResult bindingResult,
                          Model model
    ) {
        if (bindingResult.hasErrors()) {
            populateModelWithAuthorsAndGenres(model);
            return "books/edit";
        }

        bookService.insert(bookFormDto.getTitle(), bookFormDto.getAuthorId(), bookFormDto.getGenresIds());
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(@PathVariable long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));

        populateModelWithAuthorsAndGenres(model);
        model.addAttribute("book", BookFormDto.of(book));
        return "books/edit";
    }

    @PostMapping("/{id}/edit")
    public String editBook(@PathVariable long id,
                           @Valid @ModelAttribute("book") BookFormDto bookFormDto,
                           BindingResult bindingResult,
                           Model model
    ) {
        if (bindingResult.hasErrors()) {
            populateModelWithAuthorsAndGenres(model);
            return "books/edit";
        }

        bookService.update(id, bookFormDto.getTitle(), bookFormDto.getAuthorId(), bookFormDto.getGenresIds());
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    private void populateModelWithAuthorsAndGenres(Model model) {
       List<Author> authors = authorService.findAll();
       List<Genre> genres = genreService.findAll();

       model.addAttribute("authors", authors);
       model.addAttribute("genres", genres);
    }
}
