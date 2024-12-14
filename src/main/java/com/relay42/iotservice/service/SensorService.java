package com.relay42.iotservice.service;

import com.relay42.iotservice.model.SensorData;
import com.relay42.iotservice.model.TimeWindowStats;
import com.relay42.iotservice.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SensorService {

    @Autowired
    private SensorRepository repository;  // Use the correct repository


    // Method to get the statistics per time window for the given device and time range
    public List<TimeWindowStats> getSensorStats(
            String deviceId,
            String start,
            String end,
            String aggregateWindow,
            List<String> statistics
    ) {
        // Validate input parameters
        validateStatistics(statistics);

        // Call the repository method to get the statistics
        return repository.querySensorStats(deviceId, start, end, aggregateWindow, statistics);
    }

    //TODO: This can be moved to a global exception handler
    private void validateStatistics(List<String> statistics) {
        List<String> validStats = List.of("min", "max", "median", "mean");
        for (String stat : statistics) {
            if (!validStats.contains(stat)) {
                throw new IllegalArgumentException("Invalid statistic: " + stat);
            }
        }
    }

}
