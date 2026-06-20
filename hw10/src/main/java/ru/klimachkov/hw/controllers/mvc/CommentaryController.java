package ru.klimachkov.hw.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/commentaries")
public class CommentaryController {

    @GetMapping("/{id}/view")
    public String viewCommentary() {
        return "commentaries/view";
    }

    @GetMapping("/{id}/edit")
    public String editCommentary() {
        return "commentaries/edit";
    }
}
