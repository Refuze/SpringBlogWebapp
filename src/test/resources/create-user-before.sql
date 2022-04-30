delete from user_role;
delete from message;
delete from usr;

insert into usr(id, active, password, username) values
                                                    (1, true, '$2a$08$cFMMIEunDXKR5yJ2rGELAuc8Z7G27zBwqHl5KDJbQnU/www9wYKUW', 'admin'),
                                                    (2, true, '$2a$08$bA/W2/r9P.DKcNfD1UFiMelI3YtxJhXZ3FAP093UPAu4FNYIs.8v.', 'KopylovArtem');

insert into user_role(user_id, roles) values
                                          (1, 'User'), (1, 'Admin'),
                                          (2, 'User');