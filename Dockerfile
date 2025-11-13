## Multi-stage Dockerfile: build the jar with Maven then run with a slim JRE

FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy prebuilt jar (built locally with `./mvnw -DskipTests package`)
COPY target/repository-0.0.1-SNAPSHOT.jar /app/repository-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "repository-0.0.1-SNAPSHOT.jar"]