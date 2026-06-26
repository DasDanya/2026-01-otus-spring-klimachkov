package ru.klimachkov.hw.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genres")
public class GenreController {

    @GetMapping
    public String listGenres() {
        return "genres/list";
    }
}
