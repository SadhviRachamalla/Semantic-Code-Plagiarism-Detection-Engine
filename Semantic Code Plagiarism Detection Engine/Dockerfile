# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN maven_opts="-XX:+TieredCompilation -XX:TieredStopAtLevel=1" mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create lightweight runtime environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/plagiarism-engine-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
