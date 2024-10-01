from datetime import timedelta
import datetime
from airflow.operators.python import PythonOperator
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from airflow.operators.trigger_dagrun import TriggerDagRunOperator

from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}


@dag(
    'spark_load_to_bronze',
    default_args=default_args,
    description='Test spark',
    schedule_interval=timedelta(minutes=20),
    start_date=datetime.datetime.now(),
    tags=['v1'],
)
def load_to_bronze():

    start = PythonOperator(
        task_id = "start",
        python_callable=lambda: print("job started"),
    )

    python_job = SparkSubmitOperator(
        task_id="python_job",
        conn_id="spark-conn",
        packages= "com.mysql:mysql-connector-j:8.4.0",
        application="jobs/python/load_to_batch_processing_pipeline.py",
    )

    trigger_second_dag = TriggerDagRunOperator(
            task_id='trigger_spark_silver_dag',
            trigger_dag_id='spark_load_to_silver',
            wait_for_completion=False  
        )
    start >> python_job >> trigger_second_dag

load_to_bronze()