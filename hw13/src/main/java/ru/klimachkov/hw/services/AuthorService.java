package ru.klimachkov.hw.services;

import ru.klimachkov.hw.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
}
