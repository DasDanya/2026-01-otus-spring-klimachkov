insert into authors(full_name)
values ('Author_1');

insert into genres(name)
values ('Genre_1');

insert into books(title, author_id)
values ('BookTitle_1', 1);

insert into books_genres(book_id, genre_id)
values (1, 1);

insert into commentaries(text, book_id)
values ('Commentary_1', 1);

insert into users(username, password)
values ('admin', '{noop}admin'),
       ('commentaryOwner', '{noop}commentaryOwner'),
       ('otherUser', '{noop}otherUser');

insert into roles(name)
values ('ADMIN'), ('USER');

insert into users_roles(user_id, role_id)
values (1, 1), (1, 2),
       (2, 2),
       (3, 2);

insert into acl_sid(principal, sid)
values (true, 'admin'),
       (true, 'commentaryOwner'),
       (true, 'otherUser'),
       (false, 'ROLE_ADMIN');

insert into acl_class (class)
values ('ru.klimachkov.hw.models.Commentary');

insert into acl_object_identity(object_id_class, object_id_identity, owner_sid, entries_inheriting)
values (1, 1, 2, false);

insert into acl_entry(acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
values (1, 0, 2, 2, true, true, true),
       (1, 1, 2, 8, true, true, true);