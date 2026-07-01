package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.klimachkov.hw.dto.AuthorDto;
import ru.klimachkov.hw.repositories.AuthorRepository;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(AuthorDto::of);
    }
}
