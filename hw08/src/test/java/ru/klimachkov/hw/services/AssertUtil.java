package ru.klimachkov.hw.services;

import ru.klimachkov.hw.models.Author;
import ru.klimachkov.hw.models.Book;
import ru.klimachkov.hw.models.Commentary;
import ru.klimachkov.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssertUtil {

    static void assertEqual(Book actualBook, Book expectedBook) {
        assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
        assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());

        assertThat(actualBook.getAuthor())
                .usingRecursiveAssertion()
                .isEqualTo(expectedBook.getAuthor());

        assertThat(new ArrayList<>(actualBook.getGenres()))
                .usingRecursiveAssertion()
                .isEqualTo(expectedBook.getGenres());
    }

    static void assertSuccessSave(
            Book savedBook,
            String expectedTitle,
            Author expectedAuthor,
            List<Genre> expectedGenres
    ) {
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo(expectedTitle);
        assertThat(savedBook.getAuthor()).usingRecursiveAssertion().isEqualTo(expectedAuthor);
        assertThat(savedBook.getGenres()).usingRecursiveAssertion().isEqualTo(expectedGenres);
    }

    static void assertEqual(Commentary actualCommentary, Commentary expectedCommentary) {
        assertThat(actualCommentary.getId()).isEqualTo(expectedCommentary.getId());
        assertThat(actualCommentary.getText()).isEqualTo(expectedCommentary.getText());

        AssertUtil.assertEqual(actualCommentary.getBook(), expectedCommentary.getBook());
    }

    static void assertSuccessSave(
            Commentary savedCommentary,
            String expectedText,
            Book expectedBook
    ) {
        assertThat(savedCommentary).isNotNull();
        assertThat(savedCommentary.getId()).isNotNull();
        assertThat(savedCommentary.getText()).isEqualTo(expectedText);
        AssertUtil.assertEqual(savedCommentary.getBook(), expectedBook);
    }
}
