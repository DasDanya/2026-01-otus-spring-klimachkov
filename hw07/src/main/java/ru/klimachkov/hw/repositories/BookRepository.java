package ru.klimachkov.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.ListCrudRepository;
import ru.klimachkov.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends ListCrudRepository<Book, Long> {

   @Override
   @EntityGraph(value = "book-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
   List<Book> findAll();

   @Override
   @EntityGraph(value = "book-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
   Optional<Book> findById(Long id);
}
