from pyspark.sql import SparkSession
from pyspark.sql.functions import when
from pyspark.sql.functions import to_date
from pyspark.sql.functions import udf
from pyspark.sql.types import DecimalType
import datetime
from decimal import getcontext

import decimal
import json

spark = SparkSession.builder.appName("Update_Currency").getOrCreate()

app_url = "jdbc:mysql://mysqldb:3306/expense-tracker"
warehouse_url = "jdbc:mysql://mysqldb:3306/expense-tracker-warehouse"
properties = {
    "user": "user",
    "password": "password",
    "driver": "com.mysql.cj.jdbc.Driver"
}


def get_last_ids():
    df = spark.read.jdbc(warehouse_url, "batch_runs", properties=properties)
    row = df.agg({"last_user":"max", "last_transaction":"max", "last_transaction_group":"max"}).collect()[0]
    return row

def fetch_data(query, url):
    df = spark.read \
        .format("jdbc") \
        .option("url", url) \
        .option("dbtable", f"({query}) AS subquery") \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .load()
    return df

def transfer_data(df, url, table_name):
    df.write \
        .format("jdbc") \
        .option("url",url) \
        .option("dbtable", table_name) \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .mode("append") \
        .save()

def get_currency_rates():
    query = "SELECT date, rates FROM usd_daily_rate"
    rates_df = spark.read \
        .format("jdbc") \
        .option("url", warehouse_url) \
        .option("dbtable", f"({query}) AS subquery") \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .load()

    rates_dict = {row['date']: json.loads(row['rates']) for row in rates_df.collect()}
    return spark.sparkContext.broadcast(rates_dict)
    

def convert_currency(base_currency, timestamp, amount, rates_broadcast):
    rates = rates_broadcast.value
    if base_currency == "USD":
        return amount
    rate = rates.get(timestamp, None)
    if rate is None:
        raise KeyError(f"No data for date '{timestamp}'.")
    if str.lower(base_currency) not in rate.keys():
        raise KeyError(f"No data for currency {base_currency}.")
    value = rate[str.lower(base_currency)]
    return decimal.Decimal(1/value) * amount




if __name__ == "__main__":
    last_ids = get_last_ids()

    last_transaction = last_ids[1]
    transaction_query = f"SELECT timestamp, transaction_group, user_id, currency, repeat_type, status, type, amount, transaction_id, name FROM transaction_data WHERE transaction_data.transaction_id > {last_transaction}"
    transaction_df = fetch_data(transaction_query, warehouse_url)

    currencies = get_currency_rates()

    currency_udf = udf(lambda currency, date, amount : convert_currency(currency, date, amount, currencies), DecimalType(15,3))

    updated_df = transaction_df.withColumn("amount_usd", currency_udf(transaction_df.currency, to_date(transaction_df.timestamp, "yyyy-MM-dd"), transaction_df.amount))


    transfer_data(updated_df, warehouse_url, "transaction_data_silver")

    spark.stop()
    
