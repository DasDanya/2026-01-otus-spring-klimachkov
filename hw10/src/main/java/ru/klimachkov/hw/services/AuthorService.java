package ru.klimachkov.hw.services;

import ru.klimachkov.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();
}
