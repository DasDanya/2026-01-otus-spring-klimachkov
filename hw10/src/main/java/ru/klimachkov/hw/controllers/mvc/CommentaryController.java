package ru.klimachkov.hw.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books/{bookId}/commentaries")
public class CommentaryController {

    @GetMapping
    public String listCommentaries() {
        return "commentaries/list";
    }

    @GetMapping("/{id}")
    public String viewCommentary() {
        return "commentaries/view";
    }

    @GetMapping("/add")
    public String addCommentary() {
        return "commentaries/edit";
    }

    @GetMapping("/{id}/edit")
    public String editCommentary() {
        return "commentaries/edit";
    }
}
