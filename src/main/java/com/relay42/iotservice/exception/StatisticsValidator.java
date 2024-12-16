package com.relay42.iotservice.exception;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticsValidator {

    private static final List<String> VALID_STATS = List.of("min", "max", "median", "mean");

    public void validate(List<String> statistics) {
        for (String stat : statistics) {
            if (!VALID_STATS.contains(stat)) {
                throw new IllegalArgumentException("Invalid statistic: " + stat);
            }
        }
    }
}

