FROM eclipse-temurin:21-jdk
COPY target/iot-prototype-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]