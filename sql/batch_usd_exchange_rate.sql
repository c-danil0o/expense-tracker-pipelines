DROP TABLE IF EXISTS usd_daily_rate;

CREATE TABLE usd_daily_rate (
    ID BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    date DATE NOT NULL,
    rates TEXT NOT NULL
);