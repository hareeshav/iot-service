package com.relay42.iotservice.model;

public record TimeWindowStats(String time, Double min, Double max, Double median, Double mean) {

    // Utility method to update a specific statistic
    public TimeWindowStats updateStat(String statistic, Double value) {
        return switch (statistic) {
            case "min" -> new TimeWindowStats(time, value, max, median, mean);
            case "max" -> new TimeWindowStats(time, min, value, median, mean);
            case "median" -> new TimeWindowStats(time, min, max, value, mean);
            case "mean" -> new TimeWindowStats(time, min, max, median, value);
            default -> this;
        };
    }
}



