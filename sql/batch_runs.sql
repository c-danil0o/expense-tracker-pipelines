DROP TABLE IF EXISTS batch_runs;

CREATE TABLE batch_runs (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_user BIGINT NOT NULL,
    last_transaction BIGINT NOT NULL,
    last_transaction_group BIGINT NOT NULL
);

INSERT INTO batch_runs(timestamp, last_user, last_transaction, last_transaction_group)
VALUES(CURRENT_TIMESTAMP, 0,0,0);
