-- Пароль пользователя: имя почтового ящика + 1!
-- Примеры: email: petrov_petr@example.com пароль: petrov_petr1! или email: system_administrator@example.com пароль: system_administrator1!
insert into users
(id, name, email, password, role, locked)
values
(1, 'system_administrator', 'system_administrator@example.com', '$2a$12$1XIudtvAntGtgweYib2IEOdabsfpv2v4GL/j65pT25woaqfMv.5Ei', 'SYSTEM_ADMIN', false),
(2, 'office_administrator', 'office_administrator@example.com', '$2a$12$Mova.awZHvkIKcpOlzi1cOsfRTFasRLvP9IBDCk3ZgA5n32cPTqkm', 'OFFICE_ADMIN', false),
(3, 'Ivanov Ivan', 'ivanov_ivan@example.com', '$2a$12$5p/urUat78kk4mZ/WHs6a.9OZ36FVZDXLbqY05ANRrYacVfl9iDqi', 'EMPLOYEE', false),
(4, 'Petrov Petr', 'petrov_petr@example.com', '$2a$12$32egOKAmLK2bxTvduoOUteu7qG1tD0c7BFvsUMpF9up77Csc8ECHu', 'EMPLOYEE', false),
(5, 'Stepanov Stepan', 'stepanov_stepan@example.com', '$2a$12$DLk2baR0PeWioLDXBnQGXefIOZngWuTPshR.a8N5r904b9fycjvre', 'EMPLOYEE', false),
(6, 'Olgova Olga', 'olgova_olga@example.com', '$2a$12$vgBo9CSacEfTL3jZ3XjThOMZI24KKHkshwYpwi.CeC4or48agr5jS', 'EMPLOYEE', false),
(7, 'Mishina Anna', 'mishina_anna@example.com', '$2a$12$KlD5thrFHwMcUKMCHiR7au1FrF5TT30F3INt4ZKK6jRAzYPeNr8Ri', 'EMPLOYEE', false),
(8, 'Kaprizova Irina', 'kaprizova_irina@example.com', '$2a$12$.uHWde0.d7DSAsn1COC4NOW9ANbWIcgYeFZ3lYCjbgXoeNsLb2m4y', 'EMPLOYEE', false),
(9, 'Locked User', 'locked_user@example.com', '$2a$12$uVqYnzXezol3VnbxK1DGvezD85Rdpf3Ok9ZqZ4w2swKqhQr5n5QVa', 'EMPLOYEE', true);

insert into employees
(id, lastname, firstname, middlename, user_id)
values
(1,  'Иванов',    'Иван',   'Иванович', 3),
(2,  'Петров',    'Петр',   'Петрович', 4),
(3,  'Степанов',  'Степан',  null, 5),
(4,  'Ольгова',   'Ольга',  'Ивановна', 6),
(5,  'Мишина',    'Анна',   'Петровна', 7),
(6,  'Капризова', 'Ирина',  'Михайловна', 8);

insert into locations
(id, name)
values
(1, 'Open Space'),
(2, 'Зона разработки'),
(3, 'Отдел маркетинга');

insert into workplaces
(id, location_id, name, type, capacity, employee_id)
values
(1, 1, 'OH1', 'HOT_DESK', 1, null),
(2, 1, 'OH2', 'HOT_DESK', 1, null),
(3, 1, 'OH3', 'HOT_DESK', 1, null),
(4, 2, 'DI1', 'INDIVIDUAL', 1, 1),
(5, 2, 'DI2', 'INDIVIDUAL', 1, 2),
(6, 2, 'DI3', 'INDIVIDUAL', 1, 3),
(7, 3, 'MH1', 'HOT_DESK', 1, null),
(8, 3, 'MH2', 'HOT_DESK', 1, null),
(9, 3, 'MI1', 'INDIVIDUAL', 1, 4);

insert into reservations
(id, workplace_id, employee_id, start_time, end_time)
values
(1, 4, 1, parsedatetime('2026-08-04 09:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-04 18:00:00', 'yyyy-MM-dd HH:mm:ss')),
(2, 4, 1, parsedatetime('2026-08-05 08:30:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-05 18:30:00', 'yyyy-MM-dd HH:mm:ss')),
(3, 4, 1, parsedatetime('2026-08-06 08:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 12:00:00', 'yyyy-MM-dd HH:mm:ss')),

(4, 5, 2, parsedatetime('2026-08-04 10:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-04 18:00:00', 'yyyy-MM-dd HH:mm:ss')),
(5, 5, 2, parsedatetime('2026-08-05 10:10:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-05 18:10:00', 'yyyy-MM-dd HH:mm:ss')),
(6, 5, 2, parsedatetime('2026-08-06 10:20:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 18:20:00', 'yyyy-MM-dd HH:mm:ss')),

(7, 6, 3, parsedatetime('2026-08-04 10:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-04 18:00:00', 'yyyy-MM-dd HH:mm:ss')),
(8, 6, 3, parsedatetime('2026-08-05 10:10:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-05 18:10:00', 'yyyy-MM-dd HH:mm:ss')),
(9, 9, 3, parsedatetime('2026-08-06 10:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 14:00:00', 'yyyy-MM-dd HH:mm:ss')),
(10, 6, 3, parsedatetime('2026-08-06 14:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 18:00:00', 'yyyy-MM-dd HH:mm:ss')),

(11, 9, 4, parsedatetime('2026-08-04 10:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-04 18:00:00', 'yyyy-MM-dd HH:mm:ss')),
(12, 9, 4, parsedatetime('2026-08-05 10:10:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-05 18:10:00', 'yyyy-MM-dd HH:mm:ss')),
(13, 6, 4, parsedatetime('2026-08-06 10:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 14:00:00', 'yyyy-MM-dd HH:mm:ss')),
(14, 9, 4, parsedatetime('2026-08-06 14:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 18:00:00', 'yyyy-MM-dd HH:mm:ss')),

(15, 1, 5, parsedatetime('2026-08-04 10:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-04 18:00:00', 'yyyy-MM-dd HH:mm:ss')),
(16, 3, 5, parsedatetime('2026-08-05 10:30:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-05 18:30:00', 'yyyy-MM-dd HH:mm:ss')),
(17, 2, 5, parsedatetime('2026-08-06 11:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 19:00:00', 'yyyy-MM-dd HH:mm:ss')),

(18, 2, 6, parsedatetime('2026-08-04 07:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-04 15:00:00', 'yyyy-MM-dd HH:mm:ss')),
(19, 1, 6, parsedatetime('2026-08-05 08:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-05 16:00:00', 'yyyy-MM-dd HH:mm:ss')),
(20, 3, 6, parsedatetime('2026-08-06 09:00:00', 'yyyy-MM-dd HH:mm:ss'), parsedatetime('2026-08-06 17:00:00', 'yyyy-MM-dd HH:mm:ss'));