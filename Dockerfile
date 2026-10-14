FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -B verify
FROM eclipse-temurin:17-jre
RUN useradd --system --uid 10001 trainer && mkdir /app && chown trainer /app
WORKDIR /app
COPY --from=build /app/target/trainer-template-0.1.0.jar app.jar
USER trainer
ENV SERVER_ADDRESS=0.0.0.0
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
