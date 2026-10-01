# syntax=docker/dockerfile:1

# glibc-based JDK: Gradle file watching (needed for continuous builds) and the
# Kotlin compile daemon don't work on Alpine/musl.
FROM eclipse-temurin:21-jdk AS deps
WORKDIR /app

# Resolve dependencies in their own layer so they're cached across source changes.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

FROM deps AS build
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# Hot-reload dev image (used by compose.dev.yaml): continuous Gradle build +
# Spring Boot DevTools restarts. Source is kept in sync by `docker compose watch`.
FROM deps AS dev
COPY dev-entrypoint.sh ./
RUN chmod +x dev-entrypoint.sh
COPY src ./src
EXPOSE 8080
ENTRYPOINT ["./dev-entrypoint.sh"]

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S plarent && adduser -S plarent -G plarent
COPY --from=build /app/build/libs/*.jar app.jar
USER plarent
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
