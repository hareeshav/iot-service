FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/iot-service.jar /app/iot-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "iot-service.jar"]
