package ru.klimachkov.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.klimachkov.hw.dto.CommentaryDto;
import ru.klimachkov.hw.models.Commentary;

import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class R2dbcCommentaryRepository implements CommentaryRepository {

    private final R2dbcEntityOperations entityOperations;

    private final BookRepository bookRepository;

    @Override
    public Mono<CommentaryDto> findById(long id) {
        return entityOperations.select(Query.query(where("id").is(id)), Commentary.class)
                .singleOrEmpty()
                .flatMap(commentary -> bookRepository.findById(commentary.getBookId()).map(bookDto ->
                        new CommentaryDto(commentary.getId(), commentary.getText(), bookDto)));
    }

    @Override
    public Flux<CommentaryDto> findByBookId(long bookId) {
        return bookRepository.findById(bookId)
                .flatMapMany(bookDto -> selectBookCommentaries(bookId)
                        .map(commentary -> new CommentaryDto(commentary.getId(), commentary.getText(), bookDto)));
    }

    @Override
    public Flux<CommentaryDto> findByBookIdWithoutFetch(long bookId) {
        return selectBookCommentaries(bookId).map(commentary ->
                new CommentaryDto(commentary.getId(), commentary.getText()));
    }

    @Override
    public Mono<CommentaryDto> save(CommentaryDto commentaryDto) {
        return commentaryDto.getId() == 0L ? insert(commentaryDto) : update(commentaryDto);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return entityOperations.delete(Query.query(where("id").is(id)), Commentary.class).then();
    }

    private Flux<Commentary> selectBookCommentaries(long bookId) {
        return entityOperations.select(Query.query(where("bookId").is(bookId)), Commentary.class);
    }

    private Mono<CommentaryDto> insert(CommentaryDto commentaryDto) {
        Commentary commentary = new Commentary(commentaryDto.getText(), commentaryDto.getBook().getId());
        return entityOperations.insert(commentary).flatMap(insertedCommentary -> {
            commentaryDto.setId(insertedCommentary.getId());
            return Mono.just(commentaryDto);
        });
    }

    private Mono<CommentaryDto> update(CommentaryDto commentaryDto) {
        Commentary commentary =
                new Commentary(commentaryDto.getId(), commentaryDto.getText(), commentaryDto.getBook().getId());

        return entityOperations.update(commentary).thenReturn(commentaryDto);
    }
}
