package com.relay42.iotservice.generator;

import com.relay42.iotservice.model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class DataGenerator {
    @Autowired
    private KafkaTemplate<String, SensorData> kafkaTemplate;

    @Value("${spring.kafka.topic.iot-thermostats}")
    private String topic;

    private final List<String> deviceIds = List.of("device-1", "device-2", "device-3");

    @Scheduled(fixedRate = 1000)
    public void generateAndSendData() {
        for (String deviceId : deviceIds) {
            SensorData data = new SensorData(deviceId, Math.random() * 100, Instant.now());
            kafkaTemplate.send(topic, data.deviceId(), data);
            log.info("Sent to kafka: {}", data);
        }
    }
}
