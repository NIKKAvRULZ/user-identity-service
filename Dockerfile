# Step 1: Use an official JDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the executable JAR file from your target folder to the container
# Note: Ensure you run 'mvn clean package' before building the image
COPY target/*.jar app.jar

# Step 4: Expose the port your Spring Boot app runs on
EXPOSE 8081

# Step 5: Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]