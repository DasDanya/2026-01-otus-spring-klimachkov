package ru.klimachkov.hw.services;

import reactor.core.publisher.Flux;
import ru.klimachkov.hw.dto.AuthorDto;

public interface AuthorService {
    Flux<AuthorDto> findAll();
}
