# Step 1: Build the application
FROM gradle:8.5-jdk17 AS build

# Set work directory
WORKDIR /app

# Copy all project files
COPY . .

# Build the Spring Boot application
RUN gradle clean build -x test

# Step 2: Run the application
FROM eclipse-temurin:17-jre-alpine

# Set work directory
WORKDIR /app

# Copy built JAR from the build image
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port your app runs on (adjust as needed)
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
