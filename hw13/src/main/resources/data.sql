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
values ('User1_Commentary_1', 1), ('User2_Commentary_2', 1), ('User3_Commentary_3', 1),
       ('User1_Commentary_4', 2), ('User2_Commentary_5', 2), ('User3_Commentary_6', 2),
       ('User1_Commentary_7', 3), ('User2_Commentary_8', 3), ('User3_Commentary_9', 3);

insert into users(username, password)
values ('admin', '{bcrypt}$2a$12$02SJKZJ2sfG/W27M1UotWOg05ImJ4ra5aF4USuf9WzBfc7Bsyim7i'),
       ('user1', '{bcrypt}$2a$12$WW2t7udc/OEIwRBcyUi1Fugo3/4Ug6U6N1m.WupvBHiGh6AUG1KQ2'),
       ('user2', '{bcrypt}$2a$12$W4o9JMz3GH2KNp1MIKp9WORc/RbxUu0RSR7Ztp1oUpkNjNWy9xpe2'),
       ('user3', '{bcrypt}$2a$12$WVUyamzKaX42ItIC3ciTC.YUHJqPOtJY4jJOIdmYYPRy85hLnyDKG');

insert into roles(name)
values ('ADMIN'), ('USER');

insert into users_roles(user_id, role_id)
values (1, 1), (1, 2),
       (2, 2),
       (3, 2),
       (4, 2);

insert into acl_sid(principal, sid)
values (true, 'admin'),
       (true, 'user1'),
       (true, 'user2'),
       (true, 'user3'),
       (false, 'ROLE_ADMIN');

insert into acl_class (class)
values ('ru.klimachkov.hw.models.Commentary');

insert into acl_object_identity(object_id_class, object_id_identity, owner_sid, entries_inheriting)
values (1, 1, 2, false),
       (1, 2, 3, false),
       (1, 3, 4, false),
       (1, 4, 2, false),
       (1, 5, 3, false),
       (1, 6, 4, false),
       (1, 7, 2, false),
       (1, 8, 3, false),
       (1, 9, 4, false);

insert into acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
values (1, 0, 2, 2, true, true, true),
       (1, 1, 2, 8, true, true, true),
       (2, 0, 3, 2, true, true, true),
       (2, 1, 3, 8, true, true, true),
       (3, 0, 4, 2, true, true, true),
       (3, 1, 4, 8, true, true, true),
       (4, 0, 2, 2, true, true, true),
       (4, 1, 2, 8, true, true, true),
       (5, 0, 3, 2, true, true, true),
       (5, 1, 3, 8, true, true, true),
       (6, 0, 4, 2, true, true, true),
       (6, 1, 4, 8, true, true, true),
       (7, 0, 2, 2, true, true, true),
       (7, 1, 2, 8, true, true, true),
       (8, 0, 3, 2, true, true, true),
       (8, 1, 3, 8, true, true, true),
       (9, 0, 4, 2, true, true, true),
       (9, 1, 4, 8, true, true, true);