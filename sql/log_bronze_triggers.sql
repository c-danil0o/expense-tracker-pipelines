USE `expense-tracker-warehouse`;

DELIMITER $$

USE `expense-tracker-warehouse`$$
DROP TRIGGER IF EXISTS `expense-tracker-warehouse`.`after_raw_event_insert` $$
DELIMITER ;



DELIMITER $$
CREATE TRIGGER after_raw_event_insert
AFTER INSERT ON raw_event_bronze
FOR EACH ROW
BEGIN
    IF NEW.topic = 'user' THEN
        INSERT INTO user_event (event_type, timestamp, user_email, payload, event_id)
        VALUES (NEW.type, NEW.timestamp, NEW.user_email, NEW.payload, NEW.event_id);
    ELSEIF NEW.topic = 'transaction' THEN
        INSERT INTO transaction_event (event_type, timestamp, user_email, payload, event_id)
        VALUES (NEW.type, NEW.timestamp, NEW.user_email, NEW.payload, NEW.event_id);
	ELSEIF NEW.topic = 'reminder' THEN
        INSERT INTO reminder_event (event_type, timestamp, user_email, payload, event_id, transaction_group)
        VALUES (NEW.type, NEW.timestamp, NEW.user_email, NEW.payload, NEW.event_id, JSON_UNQUOTE(JSON_EXTRACT(NEW.payload, '$.category')));
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Unknown event type in RawEvent';
    END IF;
	INSERT INTO request_event (event_type, timestamp, ip_address, user_agent, os_family, device_family, feature_type, event_id)
	VALUES (NEW.type, NEW.timestamp, 
		JSON_UNQUOTE(JSON_EXTRACT(NEW.client_info, '$.remote_address')),
		JSON_UNQUOTE(JSON_EXTRACT(NEW.client_info, '$.userAgent_family')),
		JSON_UNQUOTE(JSON_EXTRACT(NEW.client_info, '$.os_family')),
		JSON_UNQUOTE(JSON_EXTRACT(NEW.client_info, '$.device_family')),
		NEW.feature_type,
		NEW.event_id
		);
END$$
DELIMITER ;