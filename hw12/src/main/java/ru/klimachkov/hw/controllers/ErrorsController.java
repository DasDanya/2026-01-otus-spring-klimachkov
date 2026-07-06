package ru.klimachkov.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorsController {

    @GetMapping("/errors/forbidden")
    public String forbidden() {
        return "errors/forbidden";
    }
}
