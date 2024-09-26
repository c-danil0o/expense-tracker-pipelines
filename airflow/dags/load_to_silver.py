
from airflow.utils.dates import days_ago
from datetime import timedelta
from datetime import datetime
import requests, json
from airflow.providers.mysql.hooks.mysql import MySqlHook
import decimal
from airflow.operators.trigger_dagrun import TriggerDagRunOperator

from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

def convert_to_usd(base_currency, date:datetime, amount):
    mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
    connection = mysql_hook.get_conn()
    cursor = connection.cursor()
    cursor.execute(f"SELECT rates FROM usd_daily_rate WHERE usd_daily_rate.date='{date.date().strftime("%Y-%m-%d")}';")
    result = cursor.fetchone()
    if result is None:
        raise KeyError(f"No data for date '{date.date()}'.")
    data = json.loads(result[0])
    if str.lower(base_currency) not in data.keys():
        raise KeyError(f"No data for currency {base_currency}.")
    value = data[str.lower(base_currency)]
    return decimal.Decimal(1/value) * amount




@dag(
    'load_to_silver',
    default_args=default_args,
    description='Copy data and transform',
    start_date=datetime.now(),
    tags=['v1'],
    schedule_interval=None
)
def load_data_into_silver():

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
        cursor.execute(f"SELECT email, country, currency, type, user_id, gender, registered_at, birthdate FROM user_data WHERE user_data.user_id > {last_user};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[4] > last_id:
                last_id = row[4]
            transfer_user_to_silver(row)
        return last_id

    def transfer_user_to_silver(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7])
        mysql_hook.run("""INSERT INTO user_data_silver(email, country, currency, type, user_id, gender, registered_at, birthdate) VALUES(%s, %s, %s,%s, %s,%s, %s, %s);""", parameters=params)
  
    @task
    def fetch_groups(last_ids):
        last_group = last_ids[0][2]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT group_id, name, user_id, budget_cap FROM transaction_group_data WHERE transaction_group_data.group_id > {last_group};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[0] > last_id:
                last_id = row[0]
            transfer_group_to_silver(row)
        return last_id

    def transfer_group_to_silver(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (row[0], row[1], row[2], row[3])
        mysql_hook.run("""INSERT INTO transaction_group_data_silver(group_id, name, user_id, budget_cap) VALUES(%s, %s, %s, %s);""", parameters=params)
        

    @task
    def fetch_transactions(last_ids):
        last_transaction = last_ids[0][1]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT timestamp, transaction_group, user_id, currency, repeat_type, status, type, amount, transaction_id FROM transaction_data WHERE transaction_data.transaction_id > {last_transaction};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[8] > last_id:
                last_id = row[8]
            transfer_transaction_to_silver(row)
        return last_id


    
    def transfer_transaction_to_silver(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        amount_usd = 0
        if row[3] == "USD":
            amount_usd = row[7]
        else:
            amount_usd = convert_to_usd(row[3], row[0], row[7])
        params = (row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], amount_usd)
        mysql_hook.run("""INSERT INTO transaction_data_silver(timestamp,transaction_group, user_id,currency, repeat_type, status ,type, amount, transaction_id, amount_usd) VALUES(%s, %s, %s,%s, %s,%s, %s, %s, %s, %s);""", parameters=params)


    trigger_second_dag = TriggerDagRunOperator(
            task_id='trigger_gold_dag',
            trigger_dag_id='load_to_gold',
            wait_for_completion=False  
        )

    ids = get_last_ids()
    fetch_users(ids) >>  fetch_groups(ids) >>  fetch_transactions(ids) >> trigger_second_dag

load_data_into_silver()