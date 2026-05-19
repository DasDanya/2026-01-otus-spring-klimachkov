package ru.klimachkov.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.SpecHints;
import org.springframework.stereotype.Repository;
import ru.klimachkov.hw.models.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        Book book = entityManager.find(Book.class, id, Map.of(SpecHints.HINT_SPEC_LOAD_GRAPH, getBookEntityGraph()));
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        query.setHint(SpecHints.HINT_SPEC_LOAD_GRAPH, getBookEntityGraph());

        return query.getResultList();
    }

    private EntityGraph<?> getBookEntityGraph() {
        return entityManager.getEntityGraph("book-entity-graph");
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }

        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book bookRef = entityManager.getReference(Book.class, id);
        entityManager.remove(bookRef);
    }
}
