DROP TABLE IF EXISTS batch_user_data;
DROP TABLE IF EXISTS batch_transaction_data;
DROP TABLE IF EXISTS batch_transaction_group_data;


CREATE TABLE batch_user_data (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    email VARCHAR(255) NOT NULL,
    birthdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR(255) NOT NULL,
	type VARCHAR(255) NOT NULL,
	country VARCHAR(255) NOT NULL,
	gender VARCHAR(255) NOT NULL
);

CREATE TABLE batch_transaction_group_data (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
	name VARCHAR(255) NOT NULL,
    group_id BIGINT NOT NULL,
    user_id BIGINT,
    budget_cap DECIMAL(15,3)
);

CREATE TABLE batch_transaction_data (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    transaction_group BIGINT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
	type VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	repeat_type VARCHAR(255) NOT NULL,
    amount DECIMAL(15,3) NOT NULL
);