package ru.klimachkov.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.klimachkov.hw.exceptions.EntityNotFoundException;
import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Genre;
import ru.klimachkov.hw.repositories.AuthorRepository;
import ru.klimachkov.hw.repositories.BookRepository;
import ru.klimachkov.hw.repositories.CommentaryRepository;
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

    private final CommentaryRepository commentaryRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public Book update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentaryRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    private Book save(String id, String title, String authorId, Set<String> genresIds) {
        if (CollectionUtils.isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));

        List<Genre> genres = genreRepository.findByIdIn(genresIds);
        if (CollectionUtils.isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        return bookRepository.save(new Book(id, title, author, genres));
    }
}
