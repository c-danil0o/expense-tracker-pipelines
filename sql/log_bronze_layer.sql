CREATE TABLE raw_event_bronze (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Corresponds to @Id and @GeneratedValue(strategy = GenerationType.IDENTITY)
    payload TEXT,                                -- Corresponds to String payload
    client_info VARCHAR(255),                    -- Corresponds to String client_info
    timestamp VARCHAR(255),                      -- Corresponds to String timestamp (can use TIMESTAMP type if it's a valid datetime)
    type VARCHAR(255),                           -- Corresponds to String type
    user_email VARCHAR(255),                     -- Corresponds to String user_email
    session_id VARCHAR(255),                     -- Corresponds to String session_id
    topic VARCHAR(255),                          -- Corresponds to String topic
    feature_type VARCHAR(255)                    -- Corresponds to String feature_type
);