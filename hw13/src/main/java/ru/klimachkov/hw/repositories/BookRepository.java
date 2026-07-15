package ru.klimachkov.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import ru.klimachkov.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends ListCrudRepository<Book, Long> {

   @Override
   @Query("SELECT b FROM Book b LEFT JOIN FETCH b.author")
   List<Book> findAll();

   @Override
   @Query("SELECT b FROM Book b LEFT JOIN FETCH b.author WHERE b.id = :id")
   Optional<Book> findById(@Param("id") Long id);
}
