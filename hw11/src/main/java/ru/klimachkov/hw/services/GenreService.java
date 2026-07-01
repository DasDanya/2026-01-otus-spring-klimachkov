package ru.klimachkov.hw.services;

import reactor.core.publisher.Flux;
import ru.klimachkov.hw.dto.GenreDto;

public interface GenreService {
    Flux<GenreDto> findAll();
}
