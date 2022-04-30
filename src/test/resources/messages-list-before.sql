delete from message;

insert into message (id, text, tag, user_id) values
                                                    (1, 'first', 'Title1', 1),
                                                    (2, 'second', 'Title2', 1),
                                                    (3, 'third', 'Title3', 1),
                                                    (4, 'forth', 'Title2', 1);
alter sequence hibernate_sequence restart with 10;