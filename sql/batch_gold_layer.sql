
DROP TABLE IF EXISTS fact_transaction_data;
DROP TABLE IF EXISTS dim_transaction_group_data;
DROP TABLE IF EXISTS dim_user_data;



CREATE TABLE dim_transaction_group_data (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
	name VARCHAR(255) NOT NULL,
    user_id BIGINT,
    group_id BIGINT,
    budget_cap DECIMAL(15,3)
);


CREATE TABLE dim_user_data (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    user_id BIGINT UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    age INT NOT NULL,
    currency VARCHAR(255) NOT NULL,
	type VARCHAR(255) NOT NULL,
	country_id BINARY(16) NOT NULL,
	gender VARCHAR(255) NOT NULL,
    CONSTRAINT FK_dim_user_data_country FOREIGN KEY (country_id)
    REFERENCES dim_country(ID)
);



CREATE TABLE fact_transaction_data (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    transaction_group BINARY(16) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
	type VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	repeat_type VARCHAR(255) NOT NULL,
    amount DECIMAL(15,3) NOT NULL,
    amount_usd DECIMAL(15,3) NOT NULL,
	 CONSTRAINT FK_fact_transaction_data_user_id FOREIGN KEY (user_id)
        REFERENCES dim_user_data(ID),
	CONSTRAINT FK_fact_transaction_data_group_id FOREIGN KEY (transaction_group)
        REFERENCES dim_transaction_group_data(ID),
	CONSTRAINT FK_fact_transaction_data_currency_id FOREIGN KEY (currency_id)
        REFERENCES dim_currency(ID)
);