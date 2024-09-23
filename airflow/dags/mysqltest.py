from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.utils.dates import days_ago
from datetime import timedelta
from airflow.providers.common.sql.operators.sql import SQLExecuteQueryOperator

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}
with DAG(
    'mysqltest',
    default_args=default_args,
    description='Test mysql connection',
    schedule_interval=timedelta(days=1),
    start_date=days_ago(2),
    tags=['example'],
) as dag:

    execute_query = SQLExecuteQueryOperator(
    task_id="execute_query",
    conn_id="mysql-server",
    sql=f"SELECT 1; SELECT * FROM raw_event_bronze;",
    split_statements=True,
    return_last=False,
)

