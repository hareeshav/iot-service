package com.relay42.iotservice.controller;

import com.relay42.iotservice.model.TimeWindowStats;
import com.relay42.iotservice.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    @Autowired
    private SensorService service;

    @GetMapping("/stats")
    public  List<TimeWindowStats> getStats(
            @RequestParam String deviceId,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "1h") String aggregateWindow, // Default to 1 hour aggregation
            @RequestParam List<String> statistics // Request the required statistics (mean, max, min, median)
    ) {
        return service.getSensorStats(deviceId, start, end, aggregateWindow, statistics);
    }

}
