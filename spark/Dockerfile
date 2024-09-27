FROM spark:3.5.2-scala2.12-java17-ubuntu

USER root

RUN set -ex; \
    apt-get update; \
    apt-get install -y python3 python3-pip curl; \
    rm -rf /var/lib/apt/lists/*

USER spark
RUN curl https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar --output /opt/bitnami/spark/jars/mysql-connector-j-9.0.0.jar
