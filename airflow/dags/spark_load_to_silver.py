from datetime import timedelta
import datetime
from airflow.operators.python import PythonOperator
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator

from airflow.providers.mysql.hooks.mysql import MySqlHook
from airflow.operators.trigger_dagrun import TriggerDagRunOperator
from airflow.decorators import dag, task

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}


@dag(
    'spark_load_to_silver',
    default_args=default_args,
    description='Test spark',
    schedule_interval=None,
    start_date=datetime.datetime.now(),
    tags=['v1'],
)
def tranform_transaction_to_silver():


    @task
    def disable_constraints():
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        mysql_hook.run("SET GLOBAL FOREIGN_KEY_CHECKS = 0;")
    
    @task
    def enable_constraints():
        mysql_hook = MySqlHook(mysql_conn_id="mysql-server", schema="expense-tracker-warehouse")
        mysql_hook.run("SET GLOBAL FOREIGN_KEY_CHECKS = 1;")

    start = PythonOperator(
        task_id = "start",
        python_callable=lambda: print("job started"),
    )
    spark_job1 = SparkSubmitOperator(
        task_id="spark-worker-copy",
        conn_id="spark-conn",
        packages= "com.mysql:mysql-connector-j:8.4.0",
        application="jobs/python/copy_to_silver.py",
    )

    spark_job2 = SparkSubmitOperator(
        task_id="spark-worker-transform",
        conn_id="spark-conn",
        packages= "com.mysql:mysql-connector-j:8.4.0",
        application="jobs/python/transform_transactions.py",
    )



    trigger_second_dag = TriggerDagRunOperator(
            task_id='trigger_spark_gold_dag',
            trigger_dag_id='spark_load_to_gold',
            wait_for_completion=False  
        )

    start >> disable_constraints() >> [spark_job1,spark_job2] >> enable_constraints() >> trigger_second_dag





tranform_transaction_to_silver()