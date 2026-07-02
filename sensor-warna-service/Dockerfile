# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline || true
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# FIX: install curl untuk healthcheck (wget tidak selalu ada di alpine)
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar

# H2 file-based database lives here (persisted via Docker volume)
RUN mkdir -p /app/data
VOLUME ["/app/data"]

EXPOSE 8083

# FIX: pakai sh -c agar JAVA_OPTS env variable terbaca oleh JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
