create table user_subscriptions (
    subscription_id int8 not null references usr,
    subscriber_id int8 not null references usr,
    primary key (subscription_id, subscriber_id)
)