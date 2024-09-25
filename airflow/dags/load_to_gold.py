from airflow.utils.dates import days_ago
from datetime import timedelta
from airflow.providers.mysql.hooks.mysql import MySqlHook
import datetime

from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

@dag(
    'load_to_gold',
    default_args=default_args,
    description='Dump data to star schema gold layer',
    start_date=datetime.datetime.now(),
    tags=['v1'],
    schedule_interval=None
)
def load_data_into_gold():

    @task
    def update_ids(ids):
        print(ids[0], ids[1], ids[2])
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (ids[1],ids[2], ids[0] )
        mysql_hook.run("""INSERT INTO batch_runs(last_transaction_group, last_user, last_transaction) VALUES(%s, %s, %s);""", parameters=params)

    @task
    def get_last_ids():
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        return mysql_hook.get_records("SELECT MAX(last_user), MAX(last_transaction), MAX(last_transaction_group) FROM batch_runs;")

    @task
    def fetch_users(last_ids):
        last_user = last_ids[0][0]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT email, country, currency, type, user_id, gender, registered_at, birthdate FROM user_data_silver WHERE user_data_silver.user_id > {last_user};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[4] > last_id:
                last_id = row[4]
            
            transform_user_data(row)
        return last_id

    def transform_user_data(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT ID FROM dim_country WHERE dim_country.code='{row[1]}';")
        result = cursor.fetchone()[0]
        
        params = (row[0], result, row[2], row[3], row[4], row[5], row[6], row[7])
        mysql_hook.run("""INSERT INTO dim_user_data(email, country_id, currency, type, user_id, gender, registered_at, birthdate) VALUES(%s, %s, %s,%s, %s,%s, %s, %s);""", parameters=params)
  
    @task
    def fetch_groups(last_ids, user_id):
        last_group = last_ids[0][2]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT group_id, name, user_id, budget_cap FROM transaction_group_data_silver WHERE transaction_group_data_silver.group_id > {last_group};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[0] > last_id:
                last_id = row[0]
            transform_groups(row)
        return last_id, user_id

    def transform_groups(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT ID FROM dim_transaction_group_data WHERE dim_transaction_group_data.group_id = {row[0]};")
        result = cursor.fetchone()
        if result is None:
            params = (row[0], row[1], row[2], row[3])
            mysql_hook.run("""INSERT INTO dim_transaction_group_data(group_id, name, user_id, budget_cap) VALUES(%s, %s, %s, %s);""", parameters=params)
        

    @task
    def fetch_transactions(last_ids, user_group_ids):
        last_transaction = last_ids[0][1]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT timestamp, transaction_group, user_id, currency, repeat_type, status, type, amount, transaction_id, amount_usd FROM transaction_data_silver WHERE transaction_data_silver.transaction_id > {last_transaction};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[8] > last_id:
                last_id = row[8]
            transform_transaction(row)
        return last_id, user_group_ids[0], user_group_ids[1]


    
    def transform_transaction(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT ID FROM dim_transaction_group_data WHERE dim_transaction_group_data.group_id = {row[1]};")
        result = cursor.fetchone()
        cursor.execute(f"SELECT ID FROM dim_user_data WHERE dim_user_data.user_id = {row[2]};")
        result2 = cursor.fetchone()
        cursor.execute(f"SELECT ID FROM dim_currency WHERE dim_currency.code = '{row[3]}';")
        result3 = cursor.fetchone()

        if result is not None and result2 is not None and result3 is not None:
            group_id = result[0]
            user_id = result2[0]
            currency_id = result3[0]

            params = (row[0], group_id, user_id, currency_id, row[4], row[5], row[6], row[7], row[9])
            mysql_hook.run("""INSERT INTO fact_transaction_data(timestamp,transaction_group, user_id,currency_id, repeat_type, status ,type, amount,  amount_usd) VALUES(%s, %s, %s, %s,%s, %s, %s, %s, %s);""", parameters=params)
        else:
            print(result, result2, result3)
            raise ValueError("Invalid warehouse constraint state!")

    ids = get_last_ids()
    last_ids = fetch_transactions(ids, fetch_groups(ids, fetch_users(ids)))
    update_ids(last_ids)


load_data_into_gold()