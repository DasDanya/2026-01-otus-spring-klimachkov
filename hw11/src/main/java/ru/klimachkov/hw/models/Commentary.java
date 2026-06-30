package ru.klimachkov.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "commentaries")
public class Commentary {

    @Id
    private long id;

    private String text;

    private long bookId;

    public Commentary(String text, long bookId) {
        this.text = text;
        this.bookId = bookId;
    }
}
