package ru.klimachkov.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.jpa.SpecHints;
import org.springframework.stereotype.Repository;
import ru.klimachkov.hw.models.Commentary;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentaryRepository implements CommentaryRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Commentary> findById(long id) {
        Commentary commentary = entityManager.find(
                Commentary.class, id, Map.of(SpecHints.HINT_SPEC_LOAD_GRAPH, getCommentaryEntityGraph()));

        if (commentary != null) {
            initializeGenres(commentary);
        }

        return Optional.ofNullable(commentary);
    }

    @Override
    public List<Commentary> findAllByBookId(long bookId) {
        TypedQuery<Commentary> query = entityManager.createQuery(
                "SELECT c FROM Commentary c WHERE c.book.id = :bookId", Commentary.class);
        query.setParameter("bookId", bookId);
        query.setHint(SpecHints.HINT_SPEC_LOAD_GRAPH, getCommentaryEntityGraph());

        List<Commentary> commentaries = query.getResultList();
        if (!commentaries.isEmpty()) {
            initializeGenres(commentaries.get(0));
        }

        return commentaries;
    }

    private void initializeGenres(Commentary commentary) {
        Hibernate.initialize(commentary.getBook().getGenres());
    }

    private EntityGraph<?> getCommentaryEntityGraph() {
        return entityManager.getEntityGraph("commentary-entity-graph");
    }

    @Override
    public Commentary save(Commentary commentary) {
        if (commentary.getId() == 0) {
            entityManager.persist(commentary);
            return commentary;
        }

        return entityManager.merge(commentary);
    }

    @Override
    public void deleteById(long id) {
        Commentary commentaryRef = entityManager.getReference(Commentary.class, id);
        entityManager.remove(commentaryRef);
    }
}
