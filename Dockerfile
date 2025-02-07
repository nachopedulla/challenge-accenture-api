FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins


COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

ENV TZ=America/Argentina/Buenos_Aires

COPY --from=builder /app/target/challenge-accenture-api-1.0-SNAPSHOT.jar challenge.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "challenge.jar"]
