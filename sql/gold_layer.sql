DROP TABLE IF EXISTS fact_transaction;
DROP TABLE IF EXISTS fact_request;
DROP TABLE IF EXISTS fact_feature_map;
DROP TABLE IF EXISTS fact_reminder;

DROP TABLE IF EXISTS dim_currency;
DROP TABLE IF EXISTS dim_country;
DROP TABLE IF EXISTS dim_transaction_group;
DROP TABLE IF EXISTS dim_feature;
DROP TABLE IF EXISTS dim_user_agent;
DROP TABLE IF EXISTS dim_request;



CREATE TABLE dim_currency (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE dim_transaction_group (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    name VARCHAR(255) NOT NULL,
    budget_cap DECIMAL(10,2)
);

CREATE TABLE dim_country (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE dim_feature (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    name VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    feature_type VARCHAR(255) NOT NULL
);

CREATE TABLE dim_user_agent (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL
);


CREATE TABLE dim_request (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    os_family VARCHAR(255),
    device_family VARCHAR(255)
);

CREATE TABLE fact_transaction (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    transaction_group_id BINARY(16) NOT NULL,
    currency_id BINARY(16),
    type VARCHAR(255) NOT NULL,
	CONSTRAINT FK_fact_transaction_transaction_group_id FOREIGN KEY (transaction_group_id)
        REFERENCES dim_transaction_group(ID),
	CONSTRAINT FK_fact_transaction_currency_id FOREIGN KEY (currency_id)
        REFERENCES dim_currency(ID)
);

CREATE TABLE fact_request (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    request_id BINARY(16) NOT NULL,
    country_id BINARY(16),
    user_agent_id BINARY(16),
	CONSTRAINT FK_fact_request_request_id FOREIGN KEY (request_id)
        REFERENCES dim_request(ID),
	CONSTRAINT FK_fact_request_country_id FOREIGN KEY (country_id)
        REFERENCES dim_country(ID)
);

CREATE TABLE fact_reminder (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    date_executed TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    repeat_rate INT NOT NULL,
    transaction_group_id BINARY(16),
	CONSTRAINT FK_fact_reminder_transaction_group_id FOREIGN KEY (transaction_group_id)
        REFERENCES dim_transaction_group(ID)
);

CREATE TABLE fact_feature_map (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    feature_id BINARY(16) NOT NULL,
    country_id BINARY(16),
    month INT NOT NULL,
    day INT NOT NULL,
    hour INT NOT NULL,
	CONSTRAINT FK_fact_feature_map_feature_id FOREIGN KEY (feature_id)
        REFERENCES dim_feature(ID),
	CONSTRAINT FK_fact_feature_map_country_id FOREIGN KEY (country_id)
        REFERENCES dim_country(ID)
);


