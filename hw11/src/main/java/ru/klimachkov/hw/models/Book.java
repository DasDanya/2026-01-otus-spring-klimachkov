package ru.klimachkov.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
public class Book {

    @Id
    private long id;

    private String title;

    private long authorId;

    public Book(String title, long authorId) {
        this.title = title;
        this.authorId = authorId;
    }
}
