# Dockerfile

# Fase di build
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app

# Copia solo il file pom.xml e scarica le dipendenze
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia tutto il resto e costruisci l'applicazione
COPY src ./src
COPY . .
RUN mvn clean package -DskipTests

# Fase di esecuzione
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia il JAR specifico dall'immagine di build
COPY --from=build /app/target/sti-jobs-0.0.1-SNAPSHOT.jar app.jar

# Imposta la variabile di ambiente per il binding del server
ENV SPRING_APPLICATION_JSON='{"server":{"address":"0.0.0.0"}}'

# Espone la porta
EXPOSE 7001

# Comando di avvio
ENTRYPOINT ["java", "-jar", "app.jar"]
