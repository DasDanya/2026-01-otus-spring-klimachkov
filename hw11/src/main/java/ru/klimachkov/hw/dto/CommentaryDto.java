package ru.klimachkov.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentaryDto {

    private long id;

    private String text;

    private BookDto book;

    public CommentaryDto(long id, String text) {
        this.id = id;
        this.text = text;
    }
}
