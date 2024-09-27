from pyspark.sql import SparkSession

spark = SparkSession.builder.appName("Bronze_layer_loader").getOrCreate()

mysql_url = "jdbc:mysql://mysqldb:3306/expense-tracker"
properties = {
    "user": "user",
    "password": "password",
    "driver": "com.mysql.jdbc.Driver"
}

df = spark.read.jdbc(mysql_url, "user", properties=properties)


df.show()