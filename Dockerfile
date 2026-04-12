# Stage 1: build & test
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean test -Dspring.profiles.active=test
RUN ./mvnw package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]