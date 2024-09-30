from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("Bronze_layer_loader").config("spark.jars", "/opt/bitnami/spark/jars/mysql-connector-j-9.0.0.jar").getOrCreate()

mysql_url = "jdbc:mysql://mysqldb:3306/expense-tracker"
properties = {
    "user": "user",
    "password": "password",
    "driver": "com.mysql.cj.jdbc.Driver"
}

df = spark.read.jdbc(mysql_url, "user", properties=properties)


df.show()