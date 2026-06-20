package ru.klimachkov.hw.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String toHome() {
        return "redirect:/books";
    }
}
