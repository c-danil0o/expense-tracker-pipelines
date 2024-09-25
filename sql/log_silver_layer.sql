DROP TABLE IF EXISTS reminder_event;
DROP TABLE IF EXISTS transaction_event;
DROP TABLE IF EXISTS user_event;
DROP TABLE IF EXISTS request_event;


CREATE TABLE user_event (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    event_type VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_email VARCHAR(255) NOT NULL,
    payload TEXT,
    event_id BIGINT,
    CONSTRAINT FK_UserEvent_EventId FOREIGN KEY (event_id)
        REFERENCES raw_event_bronze(event_id)
);

CREATE TABLE transaction_event (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    event_type VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_email VARCHAR(255) NOT NULL,
    payload TEXT,
    event_id BIGINT,
    CONSTRAINT FK_TransactionEvent_EventId FOREIGN KEY (event_id)
        REFERENCES raw_event_bronze(event_id)
);

CREATE TABLE reminder_event (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    event_type VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_email VARCHAR(255) NOT NULL,
    transaction_group VARCHAR(255),
    payload TEXT,
    event_id BIGINT,
    CONSTRAINT FK_ReminderEvent_EventId FOREIGN KEY (event_id)
        REFERENCES raw_event_bronze(event_id)
);

CREATE TABLE request_event (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    event_type VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    os_family VARCHAR(255),
    device_family VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    feature_type VARCHAR(255),
    event_id BIGINT,
    CONSTRAINT FK_RequestEvent_EventId FOREIGN KEY (event_id)
        REFERENCES raw_event_bronze(event_id)
);

