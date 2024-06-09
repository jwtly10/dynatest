FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/dynatest-0.0.1-SNAPSHOT.jar /app/dynatest-0.0.1-SNAPSHOT.jar

RUN mkdir -p /app/data

EXPOSE 8081

ENV SERVER_PORT=8081
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/local.db

ENTRYPOINT ["java", "-jar", "/app/dynatest-0.0.1-SNAPSHOT.jar"]