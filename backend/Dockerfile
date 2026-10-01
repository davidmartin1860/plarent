# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Resolve dependencies in their own layer so they're cached across source changes.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S plarent && adduser -S plarent -G plarent
COPY --from=build /app/build/libs/*.jar app.jar
USER plarent
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
