
from airflow.utils.dates import days_ago
from datetime import timedelta
from datetime import datetime
import requests, json
from airflow.models.taskinstance import TaskInstance

from airflow.providers.mysql.hooks.mysql import MySqlHook

from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

def get_currency_value(date:datetime):
    try:
        url = f"https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@{date.strftime('%Y-%m-%d')}/v1/currencies/usd.json"
        response = requests.get(url, timeout=10) 
        response.raise_for_status()
        result = response.json()
        values = result["usd"]
        return date, values

    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return None



@dag(
    'fetch_currency_catch_up_v3',
    default_args=default_args,
    description='Get currencies',
    start_date=datetime(2024, 4, 1),
    tags=['v1'],
    schedule_interval=timedelta(days=1),
    catchup=True,
)
def currency_exchange():


    @task
    def load_curr(task_instance: TaskInstance):
        date, values = get_currency_value(task_instance.execution_date)
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        connection = mysql_hook.get_conn()
        params = (date.date(), json.dumps(values))
        mysql_hook.run("""INSERT INTO usd_daily_rate(date, rates) VALUES(%s, %s);""", parameters=params)


    load_curr()

currency_exchange()