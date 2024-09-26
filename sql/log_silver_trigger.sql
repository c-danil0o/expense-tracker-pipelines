USE `expense-tracker-warehouse`;

DELIMITER $$

USE `expense-tracker-warehouse`$$
DROP TRIGGER IF EXISTS `expense-tracker-warehouse`.`after_request_event_insert` $$
DROP TRIGGER IF EXISTS `expense-tracker-warehouse`.`after_transaction_event_insert` $$
DELIMITER ;



DELIMITER $$
CREATE TRIGGER after_request_event_insert
AFTER INSERT ON request_event
FOR EACH ROW
BEGIN
		INSERT INTO dim_feature(name, code, feature_type)
		SELECT SUBSTRING_INDEX(NEW.event_type, '_', 1), NEW.event_type, NEW.feature_type
		WHERE NOT EXISTS(SELECT 1 FROM dim_feature WHERE dim_feature.code = NEW.event_type);
        
		SET @new_id = UUID_TO_BIN(UUID());
		
		INSERT INTO dim_request(ID, timestamp, ip_address, user_agent, os_family, device_family, payload, user_email)
		VALUES(
			@new_id,
			NEW.timestamp,
			NEW.ip_address,
			NEW.user_agent,
			NEW.os_family,
			NEW.device_family,
            NEW.payload,
            NEW.user_email
		);
		INSERT INTO fact_feature_map(feature_id, month, day, hour, timestamp, request_id)
        VALUES((SELECT ID FROM dim_feature WHERE dim_feature.code = NEW.event_type) , EXTRACT(MONTH FROM NEW.timestamp), EXTRACT(DAY FROM NEW.timestamp), EXTRACT(HOUR FROM NEW.timestamp), NEW.timestamp, @new_id);

END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER after_transaction_event_insert
AFTER INSERT ON transaction_event
FOR EACH ROW
BEGIN
	IF NEW.event_type = 'Transaction_Group_CREATED' THEN
		
		INSERT INTO dim_transaction_group(name, budget_cap, user_email)
		VALUES(SUBSTRING_INDEX(NEW.payload, '-', 1), (
        CASE 
			WHEN SUBSTRING_INDEX(NEW.payload, '-', -1) = '' 
			THEN 0.0
			ELSE CAST(SUBSTRING_INDEX(NEW.payload, '-', -1) AS DECIMAL(15,3))
		END
        ), NEW.user_email);
    ELSEIF NEW.event_type = 'Transaction_CREATED' THEN
		INSERT INTO fact_transaction(timestamp, currency_id, transaction_group_id, type)
		VALUES(NEW.timestamp, (SELECT ID FROM dim_currency WHERE dim_currency.code = JSON_UNQUOTE(JSON_EXTRACT(NEW.payload, '$.currency'))),
        (SELECT ID FROM dim_transaction_group WHERE dim_transaction_group.name = JSON_UNQUOTE(JSON_EXTRACT(NEW.payload, '$.category'))),
        JSON_UNQUOTE(JSON_EXTRACT(NEW.payload, '$.type')));
	END IF;
END$$
DELIMITER ;

