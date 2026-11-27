FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY src ./src
COPY data ./data
