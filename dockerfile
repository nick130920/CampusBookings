# Stage 1: Build the application using Maven
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the project, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight image for running the application
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar ./application.jar

# Expose the port the application will run on
EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "application.jar"]
