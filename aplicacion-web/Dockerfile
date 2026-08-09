FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /workspace/target/mesa-ti.war app.war
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.war --server.port=${PORT:-8080}"]
