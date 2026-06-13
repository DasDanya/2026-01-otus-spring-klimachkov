package ru.klimachkov.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "commentaries")
public class Commentary {

    @Id
    private String id;

    private String text;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @DocumentReference(lazy = true)
    private Book book;
}
