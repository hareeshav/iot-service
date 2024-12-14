package com.relay42.iotservice.model;

import java.time.Instant;

public record SensorData(String deviceId, double value, Instant timestamp) {
}
