FROM amazoncorretto:21
WORKDIR /app
COPY target/stats-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]