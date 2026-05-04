package ru.klimachkov.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.klimachkov.hw.models.Author;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private static final RowMapper<Author> AUTHOR_ROW_MAPPER = (rs, rowNum) ->
            new Author(rs.getLong("id"), rs.getString("full_name"));

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Author> findAll() {
        return jdbc.query("SELECT id, full_name FROM authors", AUTHOR_ROW_MAPPER);
    }

    @Override
    public Optional<Author> findById(long id) {
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(
                            "SELECT id, full_name FROM authors WHERE id = :id",
                            Collections.singletonMap("id", id),
                            AUTHOR_ROW_MAPPER)
            );
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }
}
