package ru.klimachkov.hw.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFound(EntityNotFoundException exception) {
        ModelAndView modelAndView = new ModelAndView("errors/entity-not-found");
        modelAndView.addObject("message", exception.getMessage()).setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
