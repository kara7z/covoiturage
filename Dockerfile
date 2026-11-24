# syntax=docker/dockerfile:1

# ---- Dev stage:
FROM maven:3.9-eclipse-temurin-21 AS dev
WORKDIR /build
COPY pom.xml .
COPY src ./src

# ---- Build stage:
FROM dev AS build
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build --chown=spring:spring /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
