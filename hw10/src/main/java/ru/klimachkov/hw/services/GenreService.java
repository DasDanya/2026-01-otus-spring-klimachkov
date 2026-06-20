package ru.klimachkov.hw.services;

import ru.klimachkov.hw.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();
}
