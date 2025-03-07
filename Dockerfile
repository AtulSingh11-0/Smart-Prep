FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN chmod +x mvnw # Give permission to execute
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Copy the .env file into the container
#COPY .env .env
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", ". .env && java -jar app.jar"]
