package ru.klimachkov.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.klimachkov.hw.dto.CommentaryFormDto;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.services.CommentaryService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/commentaries")
public class CommentaryController {

    private final CommentaryService commentaryService;

    @GetMapping
    public String listCommentaries(@PathVariable("bookId") long bookId, Model model) {
        List<Commentary> commentaries = commentaryService.findByBookId(bookId);

        model.addAttribute("bookId", bookId);
        model.addAttribute("commentaries", commentaries);
        return "commentaries/list";
    }

    @GetMapping("/{id}")
    public String viewCommentary(@PathVariable("id") long id, Model model) {
        Commentary commentary = findCommentary(id);
        model.addAttribute("commentary", commentary);
        return "commentaries/view";
    }

    @GetMapping("/add")
    public String addCommentary(@PathVariable("bookId") long bookId, Model model) {
        CommentaryFormDto commentaryFormDto = new CommentaryFormDto();
        commentaryFormDto.setBookId(bookId);
        model.addAttribute("commentary", commentaryFormDto);
        return "commentaries/edit";
    }

    @PostMapping("/add")
    public String addCommentary(@PathVariable("bookId") long bookId,
                                @Valid @ModelAttribute("commentary") CommentaryFormDto commentaryFormDto,
                                BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "commentaries/edit";
        }

        commentaryService.insert(commentaryFormDto.getText(), bookId);
        return "redirect:/books/%d/commentaries".formatted(bookId);
    }

    @GetMapping("/{id}/edit")
    public String editCommentary(@PathVariable("id") long id, Model model) {
        Commentary commentary = findCommentary(id);
        model.addAttribute("commentary", CommentaryFormDto.of(commentary));
        return "commentaries/edit";
    }

    @PostMapping("/{id}/edit")
    public String editCommentary(
            @PathVariable("bookId") long bookId,
            @PathVariable("id") long id,
            @Valid @ModelAttribute("commentary") CommentaryFormDto commentaryFormDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "commentaries/edit";
        }

        commentaryService.update(id, commentaryFormDto.getText(), bookId);
        return "redirect:/books/%d/commentaries".formatted(bookId);
    }

    @PostMapping("/{id}/delete")
    public String deleteCommentary(@PathVariable("bookId") long bookId, @PathVariable("id") long id) {
        commentaryService.deleteById(id);
        return "redirect:/books/%d/commentaries".formatted(bookId);
    }

    @NonNull
    private Commentary findCommentary(long id) {
        return commentaryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentary with id %d not found".formatted(id)));
    }
}
