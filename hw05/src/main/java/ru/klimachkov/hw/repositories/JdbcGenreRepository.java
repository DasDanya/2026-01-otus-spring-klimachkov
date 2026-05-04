package ru.klimachkov.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.klimachkov.hw.models.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private static final RowMapper<Genre> GENRE_ROW_MAPPER = (rs, rowNum) ->
            new Genre(rs.getLong("id"), rs.getString("name"));

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres", GENRE_ROW_MAPPER);
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return jdbc.query(
                "SELECT id, name FROM genres WHERE id IN (:ids)",
                Collections.singletonMap("ids", ids),
                GENRE_ROW_MAPPER);
    }
}
