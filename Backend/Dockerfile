# Utilizza una base image con JDK per buildare l'applicazione
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Utilizza una base image con JRE per eseguire l'applicazione
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 7001
ENTRYPOINT ["java", "-jar", "app.jar"]
