from pyspark.sql import SparkSession
from pyspark.sql.functions import to_date
from datetime import date, datetime

from pyspark.sql.functions import udf
from pyspark.sql.types import BinaryType
from pyspark.sql.types import IntegerType

spark = SparkSession.builder.appName("Bronze_layer_loader").getOrCreate()

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
    

def calculate_age(born):
    today = date.today()
    return today.year - born.year - ((today.month, today.day) < (born.month, born.day))


def fetch_countries():
    query = "SELECT ID, code FROM dim_country"
    ctr_df = spark.read \
        .format("jdbc") \
        .option("url", warehouse_url) \
        .option("dbtable", f"({query}) AS subquery") \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .load()

    country_dict = {row['code']: (row['ID']) for row in ctr_df.collect()}
    return spark.sparkContext.broadcast(country_dict)

def get_country_id(country_code, country_dict_broadcast):
    country_dict = country_dict_broadcast.value
    return country_dict.get(country_code, None)

def get_group_id(group_id, group_dict):
    return group_dict.value.get(group_id, None)


def fetch_groups():
    query = "SELECT ID, group_id FROM dim_transaction_group_data"
    ctr_df = spark.read \
        .format("jdbc") \
        .option("url", warehouse_url) \
        .option("dbtable", f"({query}) AS subquery") \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .load()

    group_dict = {row['group_id']: (row['ID']) for row in ctr_df.collect()}
    return spark.sparkContext.broadcast(group_dict)


def fetch_users():
    query = "SELECT ID, user_id FROM dim_user_data"
    ctr_df = spark.read \
        .format("jdbc") \
        .option("url", warehouse_url) \
        .option("dbtable", f"({query}) AS subquery") \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .load()

    user_dict = {row['user_id']: (row['ID']) for row in ctr_df.collect()}
    return spark.sparkContext.broadcast(user_dict)

def get_user_id(user_id, user_dict):
    return user_dict.value.get(user_id, None)

def fetch_currencies():
    query = "SELECT ID, code FROM dim_currency"
    ctr_df = spark.read \
        .format("jdbc") \
        .option("url", warehouse_url) \
        .option("dbtable", f"({query}) AS subquery") \
        .option("user", properties["user"]) \
        .option("password", properties["password"]) \
        .option("driver", properties["driver"]) \
        .load()

    currency_dict = {row['code']: (row['ID']) for row in ctr_df.collect()}
    return spark.sparkContext.broadcast(currency_dict)


if __name__ == "__main__":
    last_ids = get_last_ids()

    country_dict = fetch_countries()
    country_id_udf = udf(lambda code: get_country_id(code, country_dict), BinaryType())
    age_udf = udf(calculate_age, IntegerType())



    last_user = last_ids[0]
    user_query = f"SELECT email, country, currency, type, user_id, gender, registered_at, birthdate FROM user_data_silver WHERE user_data_silver.user_id > {last_user}"
    user_df = fetch_data(user_query, warehouse_url)
    
    user_df = user_df.withColumn("country_id", country_id_udf(user_df.country))
    user_df = user_df.drop("country")
    user_df = user_df.withColumn("age", age_udf(to_date(user_df.birthdate, "yyyy-MM-dd")))

    transfer_data(user_df,warehouse_url, "dim_user_data")


    group_dict = fetch_groups()
    print(group_dict.value)

    last_group = last_ids[2]
    group_query = f"SELECT group_id, name, user_id, budget_cap FROM transaction_group_data_silver WHERE transaction_group_data_silver.group_id > {last_group}"
    group_df = fetch_data(group_query, warehouse_url)
    group_df = group_df.filter(~group_df["group_id"].isin(list(group_dict.value.keys())))

    transfer_data(group_df, warehouse_url, "dim_transaction_group_data")

    currency_dict = fetch_currencies()
    user_dict = fetch_users()
    group_dict = fetch_groups()

    group_udf = udf(lambda id: get_group_id(id, group_dict), BinaryType())
    user_udf = udf(lambda user_id: get_user_id(user_id, user_dict), BinaryType())
    currency_udf = udf(lambda curr_id: currency_dict.value.get(curr_id, None), BinaryType())

    last_transaction = last_ids[1]
    transaction_query = f"SELECT timestamp, transaction_group, user_id, currency, repeat_type, status, type, amount, amount_usd, name FROM transaction_data_silver WHERE transaction_data_silver.transaction_id > {last_transaction}"
    transaction_df = fetch_data(transaction_query, warehouse_url)
    transaction_df = transaction_df.withColumn("transaction_group", group_udf(transaction_df.transaction_group))

    transaction_df = transaction_df.withColumn("user_id",user_udf(transaction_df.user_id))
    transaction_df = transaction_df.withColumn("currency_id", currency_udf(transaction_df.currency))
    transaction_df = transaction_df.drop("currency")

    transaction_df.show()



    transfer_data(transaction_df, warehouse_url, "fact_transaction_data")


    spark.stop()
    
