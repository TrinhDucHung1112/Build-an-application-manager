FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY src ./src
COPY data ./data

RUN mkdir -p out && javac -encoding UTF-8 -d out src/*.java

CMD ["java", "-cp", "out", "Main"]