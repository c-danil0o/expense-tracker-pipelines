from airflow.utils.dates import days_ago
from datetime import timedelta
from airflow.providers.mysql.hooks.mysql import MySqlHook
import datetime
from airflow.operators.trigger_dagrun import TriggerDagRunOperator

from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}


@dag(
    'load_to_bronze',
    default_args=default_args,
    description='Test mysql connection',
    schedule_interval=timedelta(minutes=20),
    start_date=datetime.datetime.now(),
    tags=['v1'],
)
def load_data_into_bronze():

    @task
    def get_last_ids():
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        return mysql_hook.get_records("SELECT MAX(last_user), MAX(last_transaction), MAX(last_transaction_group) FROM batch_runs;")

    def transfer_user_to_bronze(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7])
        mysql_hook.run("""INSERT INTO user_data(email, country, currency, type, user_id, gender, registered_at, birthdate) VALUES(%s, %s, %s,%s, %s,%s, %s, %s);""", parameters=params)


    def transfer_transaction_to_bronze(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9])
        mysql_hook.run("""INSERT INTO transaction_data(timestamp,transaction_group, user_id,currency, repeat_type, status ,type, amount, transaction_id, name) VALUES(%s, %s, %s,%s, %s,%s, %s, %s, %s, %s);""", parameters=params)

    def transfer_group_to_bronze(row):
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (row[0], row[1], row[2], row[3])
        mysql_hook.run("""INSERT INTO transaction_group_data(group_id, name, user_id, budget_cap) VALUES(%s, %s, %s, %s);""", parameters=params)
    
    @task
    def fetch_transactions(last_ids):
        last_transaction = last_ids[0][1]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT timestamp, transaction_group, user_user_id, currency, repeat_type, status, type, amount, id, name FROM transaction WHERE transaction.id > {last_transaction} AND status = 'Done';")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[8] > last_id:
                last_id = row[8]
            transfer_transaction_to_bronze(row)
        return last_id

    @task
    def fetch_users(last_ids):
        last_user = last_ids[0][0]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT email, country, currency, type, user_id, gender, registered_at, birth_date FROM user WHERE user.user_id > {last_user};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[4] > last_id:
                last_id = row[4]
            transfer_user_to_bronze(row)
        return last_id

    @task
    def fetch_groups(last_ids):
        last_group = last_ids[0][2]
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker")
        connection = mysql_hook.get_conn()
        cursor = connection.cursor()
        cursor.execute(f"SELECT id, name, user_id, budget_cap FROM transaction_group WHERE transaction_group.id > {last_group};")
        result = cursor.fetchall()
        last_id = -1
        for row in result:
            if row[0] > last_id:
                last_id = row[0]
            transfer_group_to_bronze(row)
        return last_id


    trigger_second_dag = TriggerDagRunOperator(
            task_id='trigger_silver_dag',
            trigger_dag_id='load_to_silver',
            wait_for_completion=False  
        )




    ids = get_last_ids()
    [fetch_groups(ids), fetch_users(ids), fetch_transactions(ids)] >> trigger_second_dag



load_data_into_bronze()


    

