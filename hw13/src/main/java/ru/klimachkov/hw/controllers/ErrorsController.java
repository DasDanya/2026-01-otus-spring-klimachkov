package ru.klimachkov.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorsController {

    @RequestMapping(value = "/errors/forbidden")
    public String forbidden() {
        return "errors/forbidden";
    }
}
