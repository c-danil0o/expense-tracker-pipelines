DROP VIEW IF EXISTS `Transactions per Country`;
DROP VIEW IF EXISTS `view_user_age_group_spending`;
DROP VIEW IF EXISTS `Transactions per Group`;
DROP VIEW IF EXISTS `Category through time`;
DROP VIEW IF EXISTS `Feature map per month`;
DROP VIEW IF EXISTS `view_currency_usage`;
DROP VIEW IF EXISTS `view_user_gender_spending`;
DROP VIEW IF EXISTS `view_top_frequent_transaction_names`;

CREATE VIEW `Transactions per Country` AS
SELECT c.name as Country, COUNT(t.ID) as Transactions
FROM fact_transaction_data t, dim_user_data u, dim_country c
WHERE t.user_id = u.ID AND u.country_id = c.ID
GROUP BY c.name;


CREATE VIEW view_user_age_group_spending AS
SELECT 
    CASE 
        WHEN u.age BETWEEN 18 AND 24 THEN '18-24'
        WHEN u.age BETWEEN 25 AND 34 THEN '25-34'
        WHEN u.age BETWEEN 35 AND 44 THEN '35-44'
        WHEN u.age BETWEEN 45 AND 54 THEN '45-54'
        ELSE '55+' 
    END AS age_group,
    COUNT(ft.ID) AS total_transactions,
    SUM(ft.amount_usd) AS total_spent_usd
FROM 
    fact_transaction_data ft
JOIN 
    dim_user_data u ON ft.user_id = u.ID
GROUP BY 
    age_group;

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


CREATE VIEW view_currency_usage AS
SELECT 
    dc.name AS currency_name,
    dc.code AS currency_code,
    COUNT(ft.ID) AS number_of_transactions,
    SUM(ft.amount) AS total_amount,
    SUM(ft.amount_usd) AS total_amount_usd
FROM 
    fact_transaction_data ft
JOIN 
    dim_currency dc ON ft.currency_id = dc.ID
GROUP BY 
    dc.name, dc.code;
    
CREATE VIEW view_user_gender_spending AS
SELECT 
    u.Gender,
    COUNT(ft.ID) AS total_transactions,
    SUM(ft.amount_usd) AS total_spent_usd,
    AVG(ft.amount_usd) AS avg_transaction_usd
FROM 
    fact_transaction_data ft
JOIN 
    dim_user_data u ON ft.user_id = u.ID
GROUP BY 
    u.gender;

CREATE VIEW view_top_frequent_transaction_names AS
SELECT 
    ft.name AS transaction_name,
    tg.name AS transaction_group_name,
    COUNT(ft.ID) AS occurrence_count,
    SUM(ft.amount_usd) AS total_spent_usd
FROM 
    fact_transaction_data ft
JOIN 
    dim_transaction_group_data tg ON ft.transaction_group = tg.ID
GROUP BY 
    ft.name, tg.name
HAVING 
    COUNT(ft.ID) > 1
ORDER BY 
    occurrence_count DESC
LIMIT 10;


CREATE OR REPLACE VIEW most_common_transaction_names AS
WITH ranked_transactions AS (
    SELECT 
        tg.name AS Group_name,
        ft.name AS Transaction_name,
        COUNT(*) AS transaction_count,
        ROW_NUMBER() OVER (PARTITION BY tg.name ORDER BY COUNT(*) DESC) AS rank2
    FROM fact_transaction_data ft
    JOIN dim_transaction_group_data tg ON ft.transaction_group = tg.ID
    GROUP BY tg.name, ft.name
)
SELECT
    Group_name,
    Transaction_name,
    transaction_count
FROM ranked_transactions
WHERE rank2 <= 3;


CREATE OR REPLACE VIEW most_used_features AS
SELECT 
    df.name AS feature_name,
    COUNT(ffm.request_id) AS request_count
FROM fact_feature_map ffm
JOIN dim_feature df ON ffm.feature_id = df.ID
GROUP BY df.name
ORDER BY request_count DESC;

CREATE OR REPLACE VIEW premium_feature AS
SELECT 
    df.name AS feature_name,
    df.code as feature_code,
    COUNT(ffm.request_id) AS feature_usage
FROM fact_feature_map ffm
JOIN dim_feature df ON ffm.feature_id = df.ID
WHERE df.feature_type = 'Premium'
GROUP BY df.name, df.code
ORDER BY feature_usage DESC;



CREATE OR REPLACE VIEW request_activity_by_time AS
SELECT 
	dr.timestamp,
    COUNT(dr.ID) AS request_count
FROM dim_request dr
GROUP BY dr.timestamp
ORDER BY dr.timestamp;

CREATE OR REPLACE VIEW top_users AS
SELECT 
	dr.user_email,
	COUNT(dr.ID) AS request_count
FROM dim_request dr
WHERE dr.user_email != 'anonymousUser'
GROUP BY dr.user_email;



CREATE OR REPLACE VIEW new_users_per_month AS
SELECT 
    ffm.month,
    COUNT(DISTINCT dr.user_email) AS new_user_count
FROM fact_feature_map ffm
JOIN dim_request dr ON ffm.request_id = dr.ID
JOIN dim_feature df ON ffm.feature_id = df.ID
WHERE df.code = 'User_Register_EXECUTED'
GROUP BY ffm.month
ORDER BY ffm.month;
