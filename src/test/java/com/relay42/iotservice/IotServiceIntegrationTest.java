package com.relay42.iotservice;

import com.relay42.iotservice.model.SensorData;
import com.relay42.iotservice.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IotServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SensorRepository sensorRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Seed data into InfluxDB
        sensorRepository.saveSensorData(new SensorData("device123", 23.5, Instant.now().minusSeconds(3600)));
        sensorRepository.saveSensorData(new SensorData("device123", 25.0, Instant.now().minusSeconds(1800)));
        sensorRepository.saveSensorData(new SensorData("device123", 22.0, Instant.now()));
    }

    @Test
    void testGetStats() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/sensors/stats")
                        .param("deviceId", "device123")
                        .param("start", Instant.now().minusSeconds(7200).toString())
                        .param("end", Instant.now().toString())
                        .param("aggregateWindow", "30m")
                        .param("statistics", "max", "min")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].time").exists());
    }
}

