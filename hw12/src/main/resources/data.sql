insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);

insert into commentaries(text, book_id)
values ('Commentary_1', 1), ('Commentary_2', 1), ('Commentary_3', 1),
       ('Commentary_4', 2), ('Commentary_5', 2), ('Commentary_6', 2),
       ('Commentary_7', 3), ('Commentary_8', 3), ('Commentary_9', 3);

insert into users(username, password)
values ('user', '{bcrypt}$2a$12$T5DrWYeoZcDpdnNarWw66egLB8sFwpOBT1TqHYsh.Nr0QJJrD.4Bm'),
       ('admin', '{bcrypt}$2a$12$02SJKZJ2sfG/W27M1UotWOg05ImJ4ra5aF4USuf9WzBfc7Bsyim7i');

insert into roles(name)
values ('USER'), ('ADMIN');

insert into users_roles(user_id, role_id)
values (1, 1),
       (2, 1), (2, 2);