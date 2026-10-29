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

# Hot-reload dev image (used by compose.dev.yaml): continuous Gradle build +
# Spring Boot DevTools restarts. Source is kept in sync by `docker compose watch`.
FROM deps AS dev
COPY dev-entrypoint.sh ./
RUN chmod +x dev-entrypoint.sh
COPY src ./src
EXPOSE 8080
ENTRYPOINT ["./dev-entrypoint.sh"]

# Production build: compile to a GraalVM native executable. Uses far less memory
# than the JVM (~40 MiB vs ~240 MiB) and starts in well under a second, at the
# cost of a slow, memory-hungry build (several minutes, ~7 GB peak).
FROM ghcr.io/graalvm/native-image-community:21 AS build
# gradlew needs xargs, which this Oracle Linux base image doesn't ship.
RUN microdnf install -y findutils && microdnf clean all
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true
COPY src ./src
RUN ./gradlew --no-daemon nativeCompile -x test

# The native binary links glibc and zlib dynamically, so distroless/base won't do.
FROM debian:12-slim AS runtime
WORKDIR /app
RUN groupadd -r plarent && useradd -r -g plarent plarent \
    && mkdir -p /data/plant-images && chown plarent:plarent /data/plant-images
COPY --from=build /app/build/native/nativeCompile/backend app
ENV PLANT_IMAGE_DIR=/data/plant-images
USER plarent
EXPOSE 8080
# Max heap defaults to 64m (set at build time); pass e.g. -Xmx128m as the command to override.
ENTRYPOINT ["/app/app"]
