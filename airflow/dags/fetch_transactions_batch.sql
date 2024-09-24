SELECT id, timestamp, transaction_group, user_user_id, currency, repeat_type, status, type, amount
FROM transaction WHERE transaction.id > {{params.last_transaction_id}};