FROM maven:3.8.4-openjdk-17-slim as builder1
WORKDIR /app
COPY . /app/.

RUN mvn -f /app/pom.xml clean package -U -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder1 /app/kafkaStream/target/*.jar /app/*.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/*.jar"]