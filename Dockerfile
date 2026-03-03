# Stage 1: Build the application using Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
# Copy your project files
COPY pom.xml .
COPY src ./src
# Build the JAR inside the cloud environment
RUN mvn clean package -DskipTests

# Stage 2: Create the final production image using a supported JDK
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Copy the built JAR from the first stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]