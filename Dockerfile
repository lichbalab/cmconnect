# Use OpenJDK 21 with Alpine as the base image
FROM eclipse-temurin:21-alpine

# Set the working directory in the Docker image
WORKDIR /ksc

# Copy the entire project source
COPY modules/config-rest-api/target/config-rest-api-*.jar /ksc/config-rest-api.jar

# Specify the entry point for the application
ENTRYPOINT ["java","-jar","/ksc/config-rest-api.jar"]