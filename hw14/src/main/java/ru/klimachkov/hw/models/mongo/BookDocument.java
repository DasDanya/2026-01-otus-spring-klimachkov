package ru.klimachkov.hw.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
public class BookDocument {

    @Id
    private String id;

    private String title;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DocumentReference(lazy = true)
    private AuthorDocument author;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DocumentReference(lazy = true)
    private List<GenreDocument> genres;
}
