package ru.klimachkov.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.Optional;

public interface CommentaryRepository extends CrudRepository<Commentary, Long> {

    @EntityGraph(value = "commentary-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Commentary> findByBookId(long bookId);

    @Override
    @EntityGraph(value = "commentary-entity-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Commentary> findById(Long id);
}
