FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/repository-0.0.1-SNAPSHOT.jar /app/repository-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "repository-0.0.1-SNAPSHOT.jar"]