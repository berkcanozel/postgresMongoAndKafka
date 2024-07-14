
# Use the official Maven image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8.1-openjdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Use the official OpenJDK image to run the application
# https://hub.docker.com/_/openjdk
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/kafka-example-0.0.1-SNAPSHOT.jar /usr/local/lib/kafka-example.jar
EXPOSE 8101
ENTRYPOINT ["java","-jar","/usr/local/lib/kafka-example.jar"]

# Dockerfile
FROM mongo:latest

COPY mongo-init.js /docker-entrypoint-initdb.d/
