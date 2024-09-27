DROP VIEW IF EXISTS `Transactions per Country`;
DROP VIEW IF EXISTS `Transaction amount age group`;
DROP VIEW IF EXISTS `Transactions per Group`;
DROP VIEW IF EXISTS `Category through time`;
DROP VIEW IF EXISTS `Feature map per month`;

CREATE VIEW `Transactions per Country` AS
SELECT c.name as Country, COUNT(t.ID) as Transactions
FROM fact_transaction_data t, dim_user_data u, dim_country c
WHERE t.user_id = u.ID AND u.country_id = c.ID
GROUP BY c.name;


CREATE VIEW `Transaction amount age group` AS
SELECT a.age_group, AVG(t.amount_usd) Average FROM
(SELECT u.ID user_id, CASE WHEN u.age < 18 then '<18'
			WHEN u.age between 18 and 25 then '18-25'
            when u.age between 25 and 40 then '25-40'
            when u.age > 40 then '>40' END age_group from dim_user_data u) a, fact_transaction_data t, dim_user_data us
WHERE t.user_id = us.ID AND a.user_id = us.ID AND t.type = 'Expense' 
GROUP BY a.age_group;

CREATE VIEW `Transactions per Group` AS
SELECT g.name Category, t.timestamp, t.type, t.amount_usd, t.amount, c.name
FROM fact_transaction_data t, dim_transaction_group_data g, dim_currency c
WHERE g.ID = t.transaction_group and c.ID = t.currency_id;

CREATE VIEW `Category through time` AS
SELECT g.name Category, SUM(t.amount_usd), t.timestamp
FROM fact_transaction_data t, dim_transaction_group_data g
WHERE g.ID = t.transaction_group
GROUP BY g.name, t.timestamp;


CREATE VIEW `Feature map per month` AS
SELECT ffm.month,ffm.timestamp,
COUNT(CASE WHEN df.name='User' THEN 1 END) AS User,
COUNT(CASE WHEN df.name='Transaction' THEN 1 END) as Transaction,
COUNT(CASE WHEN df.name='Reminder' THEN 1 END) as Reminder
FROM fact_feature_map ffm
JOIN dim_feature df ON ffm.feature_id = df.ID
GROUP BY ffm.month, ffm.timestamp
ORDER BY ffm.month;

