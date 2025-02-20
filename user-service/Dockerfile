# Step 1: Build the project with Maven
FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Run Maven to clean and package the project
RUN mvn clean package -DskipTests

# Step 2: Use a JDK image to run the application
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the generated JAR file from the build step
COPY --from=builder /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your service will run on
EXPOSE 8081

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
