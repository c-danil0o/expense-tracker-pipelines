FROM openjdk:22-jdk
MAINTAINER danilo.cvijetic@vega.local
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]