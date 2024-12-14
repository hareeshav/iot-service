package com.relay42.iotservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.relay42.iotservice.model.SensorData;
import com.relay42.iotservice.repository.SensorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SensorListener {

    private final SensorRepository sensorRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public SensorListener(SensorRepository sensorRepository, ObjectMapper objectMapper) {
        this.sensorRepository = sensorRepository;
        this.objectMapper = objectMapper;
    }

    // Kafka Listener to consume messages from the 'iot-thermostats' topic
    @KafkaListener(topics = "${spring.kafka.topic.iot-thermostats}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenToSensorData(ConsumerRecord<String, String> record) {
        try {
            // Convert the Kafka message (JSON) into SensorData object
            String message = record.value();
            SensorData sensorData = objectMapper.readValue(message, SensorData.class);

            // Save the sensor data to InfluxDB
            sensorRepository.saveSensorData(sensorData);

            System.out.println("Sensor data saved to InfluxDB: " + sensorData);

        } catch (Exception e) {
           //log.error(e.getMessage());
            // Handle any exceptions (e.g., invalid JSON, InfluxDB errors)
        }
    }
}

