# Stage 1: Build the application using Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
# Copy only the pom.xml and source code to keep the build efficient
COPY pom.xml .
COPY src ./src
# Build the application and skip tests for a faster deployment
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built JAR from the 'build' stage above
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]