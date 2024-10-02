from pyspark.sql import SparkSession

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
    



if __name__ == "__main__":
    last_ids = get_last_ids()

    last_user = last_ids[0]
    user_query = f"SELECT email, country, currency, type, user_id, gender, registered_at, birth_date FROM user WHERE user.user_id > {last_user}"
    user_df = fetch_data(user_query, app_url)
    transfer_data(user_df.withColumnRenamed("birth_date", "birthdate") ,warehouse_url, "user_data")

    last_transaction = last_ids[1]
    transaction_query = f"SELECT timestamp, transaction_group, user_user_id, currency, repeat_type, status, type, amount, id, name FROM transaction WHERE transaction.id > {last_transaction} AND status = 'Done'"
    transaction_df = fetch_data(transaction_query, app_url)
    transfer_data(transaction_df.withColumnsRenamed({"user_user_id": "user_id", "id":"transaction_id"}), warehouse_url, "transaction_data")

    last_group = last_ids[2]
    group_query = f"SELECT id, name, user_id, budget_cap FROM transaction_group WHERE transaction_group.id > {last_group}"
    group_df = fetch_data(group_query, app_url)
    transfer_data(group_df.withColumnRenamed("id", "group_id"), warehouse_url, "transaction_group_data")

    spark.stop()
    
