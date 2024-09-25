
DROP TABLE IF EXISTS transaction_data_silver;
DROP TABLE IF EXISTS user_data_silver;
DROP TABLE IF EXISTS transaction_group_data_silver;


CREATE TABLE user_data_silver (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    user_id BIGINT UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR(255) NOT NULL,
	type VARCHAR(255) NOT NULL,
	country VARCHAR(255) NOT NULL,
	gender VARCHAR(255) NOT NULL
);

CREATE TABLE transaction_group_data_silver (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
	name VARCHAR(255) NOT NULL,
    group_id BIGINT UNIQUE NOT NULL,
    user_id BIGINT,
    budget_cap DECIMAL(15,3)
);

CREATE TABLE transaction_data_silver (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    transaction_id BIGINT NOT NULL,
    transaction_group BIGINT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
	type VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	repeat_type VARCHAR(255) NOT NULL,
    amount DECIMAL(15,3) NOT NULL,
    amount_usd DECIMAL(15,3) NOT NULL,
	 CONSTRAINT FK_transaction_data_silver_user_id FOREIGN KEY (user_id)
        REFERENCES user_data_silver(user_id),
	CONSTRAINT FK_transaction_data_silver_group_id FOREIGN KEY (transaction_group)
        REFERENCES transaction_group_data_silver(group_id)
);