USE `expense-tracker-warehouse`;

DELIMITER $$

SOURCE ./log_silver_layer.sql
SOURCE ./log_bronze_triggers.sql
SOURCE ./log_gold_layer.sql
SOURCE ./log_silver_triggers.sql

SOURCE ./countries.sql
SOURCE ./currencies.sql

SOURCE ./batch_runs.sql
SOURCE ./batch_bronze_layer.sql
SOURCE ./batch_silver_layer.sql
SOURCE ./batch_usd_exchange_rate.sql
SOURCE ./batch_gold_layer.sql

DELIMITER ;