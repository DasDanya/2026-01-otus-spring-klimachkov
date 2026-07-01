package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.klimachkov.hw.dto.GenreDto;
import ru.klimachkov.hw.repositories.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(GenreDto::of);
    }
}
