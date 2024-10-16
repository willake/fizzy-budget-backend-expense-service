# Use a base image with JDK
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the application jar to the container using a wildcard
COPY target/expense-service-*.jar /app/expense-service.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/expense-service.jar"]
