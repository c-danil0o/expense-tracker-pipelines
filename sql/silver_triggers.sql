DELIMITER $$
CREATE TRIGGER after_raw_event_insert_user
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
END$$
DELIMITER ;