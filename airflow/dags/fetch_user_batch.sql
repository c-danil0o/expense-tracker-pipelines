SELECT user_id, email, country, currency, type
FROM user WHERE user.user_id > {{params.last_user_id}};