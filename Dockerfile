# Use lightweight Java 21 image
FROM eclipse-temurin:21-jdk

# App directory inside container
WORKDIR /app

# Copy project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the Spring Boot jar
RUN ./mvnw clean package -DskipTests

# Expose port used by Spring Boot
EXPOSE 8080

# Start the application
CMD ["java","-jar","target/gnn-news-network-0.0.1-SNAPSHOT.jar"]
