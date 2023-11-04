# Use OpenJDK 21 with Alpine as the base image
FROM eclipse-temurin:21-alpine

# Set the working directory in the Docker image
WORKDIR /ksc

# Install Maven
RUN apk add --no-cache maven

# Copy the entire project source
COPY pom.xml /ksc/
COPY modules /ksc/modules/

# Build the entire project
RUN mvn clean package -Dmaven.test.skip=true

# Set the path to the config-rest-api JAR file
ARG JAR_FILE=/ksc/modules/config-rest-api/target/*.jar

# Copy the config-rest-api JAR file into the image
COPY ${JAR_FILE} /ksc/config-rest-api.jar

# Specify the entry point for the application
ENTRYPOINT ["java","-jar","/ksc/config-rest-api.jar"]