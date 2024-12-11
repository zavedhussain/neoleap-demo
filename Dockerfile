# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Expose the application port
EXPOSE 8000

# Copy the Maven build artifact into the container
COPY target/ecommerce-demo.jar ecommerce-demo.jar

# Set the working directory
WORKDIR /app

# Run the application
ENTRYPOINT ["java", "-jar", "ecommerce-demo.jar"]
