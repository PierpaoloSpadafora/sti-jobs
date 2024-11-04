FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/sti-jobs-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_APPLICATION_JSON='{"server":{"address":"0.0.0.0"}}'

EXPOSE 7001

ENTRYPOINT ["java", "-jar", "app.jar"]
