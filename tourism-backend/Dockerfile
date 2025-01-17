# Use an OpenJDK base image
FROM openjdk:21-jdk-slim
# RUN apt-get update && apt-get install -y curl

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/tourism-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
