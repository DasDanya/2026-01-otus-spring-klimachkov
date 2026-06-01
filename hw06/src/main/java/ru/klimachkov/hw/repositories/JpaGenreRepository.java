package ru.klimachkov.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.klimachkov.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        TypedQuery<Genre> query = entityManager.createQuery(
                "SELECT g FROM Genre g WHERE g.id IN (:ids)", Genre.class);
        query.setParameter("ids", ids);

        return query.getResultList();
    }
}
