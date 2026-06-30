package ru.klimachkov.hw.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BookController {

    @GetMapping
    public String listBooks() {
       return "books/list";
    }

    @GetMapping("/{id}")
    public String viewBook() {
        return "books/view";
    }

    @GetMapping("/add")
    public String addBook() {
        return "books/edit";
    }

    @GetMapping("/{id}/edit")
    public String editBook() {
        return "books/edit";
    }
}
