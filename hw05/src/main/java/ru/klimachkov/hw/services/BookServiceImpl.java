package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.repositories.AuthorRepository;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book insert(String title, long authorId, Set<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Override
    public Book update(long id, String title, long authorId, Set<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    private Book save(long id, String title, long authorId, Set<Long> genresIds) {
        if (CollectionUtils.isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        List<Genre> genres = genreRepository.findAllByIds(genresIds);
        if (CollectionUtils.isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        return bookRepository.save(new Book(id, title, author, genres));
    }

    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }
}
