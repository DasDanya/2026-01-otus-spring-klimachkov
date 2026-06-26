package ru.klimachkov.hw.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.Optional;

public interface CommentaryRepository extends CrudRepository<Commentary, Long> {

    @Query("SELECT c FROM Commentary c LEFT JOIN FETCH c.book LEFT JOIN FETCH c.book.author WHERE c.book.id = :bookId")
    List<Commentary> findByBookId(@Param("bookId") long bookId);

    @Query("SELECT c FROM Commentary c WHERE c.book.id = :bookId")
    List<Commentary> findByBookIdWithoutFetch(@Param("bookId") long bookId);

    @Override
    @Query("SELECT c FROM Commentary c LEFT JOIN FETCH c.book LEFT JOIN FETCH c.book.author WHERE c.id = :id")
    Optional<Commentary> findById(@Param("id") Long id);
}
