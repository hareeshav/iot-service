# IoT Data Processing Pipeline with Secure Web Service

## Task Overview

The task involves building a scalable pipeline for processing IoT data, and implementing a secure web service for
querying sensor readings (e.g., average, median, max, min values) of specific sensors or groups of sensors within a
defined timeframe.

## Implementation Details

A sample data generator class is used to mock the data from 3 IoT devices every second. This data is captured to
KafkaTopic. The kafka listener is configured to read the messages
from the topic to influxDB. API is then created to query this influxDB based on statistics and time window.

## Technology Stack

- **Java**: The backend application is implemented using Java 21.
- **Spring Boot**: Used for building the backend REST API and secure web service.
- **Kafka**: A message broker used to handle real-time data streams from IoT devices.
- **InfluxDB**: A time-series database used to store and query sensor data. Currently for demo cloud hosted service is
  used
- **Docker Compose**: For containerizing the application, Kafka.

## Local Setup

### Prerequisites

Before running the application locally, make sure you have the following installed on your machine:

- **Docker**: For containerizing and running the services.
- **Maven**: For building and running the Spring Boot application.

### Steps to Run the Application Locally

1. **Start Docker Services (Kafka and InfluxDB)**
   Run the following command to start Kafka, Zookeeper, and InfluxDB containers using Docker Compose:

   ```bash
   docker-compose up
   mvn clean package
   mvn spring-boot:run

API Endpoints

1. **Sensor Data Query Endpoint**
   The web service allows you to query sensor statistics (e.g., mean, median, max, min) for a specific device or group
   of devices within a defined timeframe.

Endpoint:
`GET /api/sensors/stats`

Query Parameters:

* deviceId: ID of the sensor device (e.g., device123).
* start: Start time in ISO 8601 format (e.g., 2023-12-01T00:00:00Z).
* end: End time in ISO 8601 format (e.g., 2024-12-31T23:59:59Z).
* statistics: Comma-separated list of statistics (e.g., max, min, median, mean).
* aggregateWindow: The window for aggregation (e.g., 1h for 1hour).
  `Example Request:
  GET http://localhost:8080/api/sensors/stats?deviceId=device123&start=2023-12-01T00%3A00%3A00Z&end=2024-12-31T23%3A59%3A59Z&statistics=max,%20min,median,mean&aggregateWindow=1h
  Example Response:
  [
  {
  "time": "2023-12-01T00:00:00Z",
  "mean": 23.5,
  "max": 25.0,
  "min": 22.0
  }
  ]`

## 2. Authentication Endpoint

An Auth controller is introduced to demonstrate a secure web service. It returns a JWT token, which must be passed in
the Authorization header for protected API endpoints.

`Endpoint:
POST /api/login
Example Request:
{
"username": "user",
"password": "password"
}
Example Response:
{
"token": "your-jwt-token"
}
curl -H "Authorization: Bearer your-jwt-token" "http://localhost:8080/api/sensors/stats?deviceId=device123&start=2023-12-01T00%3A00%3A00Z&end=2024-12-31T23%3A59%3A59Z&statistics=max,%20min,median,mean&aggregateWindow=30m"
`

### Further Improvements

1. Logging & Profiling: Adding enhanced logging mechanisms (e.g., log levels, custom log formats) and profiling tools
   for performance analysis.
2. Schema for Kafka: Defining a schema for Kafka messages to ensure consistency and validation of incoming data.
3. Unit Testing: Adding more unit tests for critical components of the application.
4. API Documentation:  Integrating Swagger/OpenAPI for automatic API documentation and better visibility of the
   available
   endpoints.


