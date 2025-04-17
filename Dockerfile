# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Maven build file to the container
COPY /target/fleet-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]