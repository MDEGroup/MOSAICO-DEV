## Multi-stage Dockerfile: build the jar with Maven then run with a slim JRE

FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /workspace

# Copy the project files and run the Maven wrapper to build the jar
COPY . .
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app
# Copy the jar produced in the builder stage
COPY --from=builder /workspace/target/repository-0.0.1-SNAPSHOT.jar /app/repository-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "repository-0.0.1-SNAPSHOT.jar"]