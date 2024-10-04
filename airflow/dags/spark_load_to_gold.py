from datetime import timedelta
import datetime
from airflow.operators.python import PythonOperator
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator

from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}


@dag(
    'spark_load_to_gold',
    default_args=default_args,
    description='Test spark',
    schedule_interval=None,
    start_date=datetime.datetime.now(),
    tags=['v1'],
)
def load_to_gold():

    start = PythonOperator(
        task_id = "start",
        python_callable=lambda: print("job started"),
    )
    spark_job1 = SparkSubmitOperator(
        task_id="spark-worker-gold",
        conn_id="spark-conn",
        packages= "com.mysql:mysql-connector-j:8.4.0",
        application="jobs/python/transform_to_star.py",
    )


    start >> spark_job1   




load_to_gold()