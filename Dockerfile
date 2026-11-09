# Build Spring Boot backend
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY backend/ .

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Run Spring Boot application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]