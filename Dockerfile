# Stage 1: Build stage
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Step 1: Copy only the pom.xml first
COPY pom.xml .

# Step 2: Download dependencies (this is the "heavy" part)
# This layer will be cached and only re-run if you change pom.xml
RUN mvn dependency:go-offline -B

# Step 3: Now copy the source code
COPY src ./src

# Step 4: Package the app
# Since dependencies are already in the image, this will be very fast
RUN mvn package -DskipTests

# Stage 2: Final Run stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Use the environment variable for port binding (Crucial for Render/Azure)
ENV PORT=8085
EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]