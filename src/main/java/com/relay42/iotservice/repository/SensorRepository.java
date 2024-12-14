package com.relay42.iotservice.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.relay42.iotservice.model.SensorData;
import com.relay42.iotservice.model.TimeWindowStats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
public class SensorRepository {

    private final InfluxDBClient influxDBClient;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Value("${influxdb.org}")
    private String org;

    public SensorRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    private static final String QUERY_TEMPLATE = """
        from(bucket: "%s")
        |> range(start: time(v: "%s"), stop: time(v: "%s"))
        |> filter(fn: (r) => r["_measurement"] == "sensor_data")
        |> filter(fn: (r) => r["deviceId"] == "%s")
        |> aggregateWindow(every: %s, fn: %s, createEmpty: false)
    """;

    public List<TimeWindowStats> querySensorStats(
            String deviceId,
            String start,
            String end,
            String aggregateWindow,
            List<String> statistics
    ) {
        // Map to collect statistics grouped by time
        Map<String, TimeWindowStats> statsByTime = new TreeMap<>();

        for (String statistic : statistics) {
            // Build the query dynamically
            String query = buildQuery(deviceId, start, end, aggregateWindow, statistic);

            // Execute the query and process the result
            List<FluxTable> fluxTables = executeQuery(query);
            updateStats(statsByTime, fluxTables, statistic);
        }

        // Return the combined results
        return new ArrayList<>(statsByTime.values());
    }

    private String buildQuery(String deviceId, String start, String end, String aggregateWindow, String statistic) {
        return QUERY_TEMPLATE.formatted(bucket, start, end, deviceId, aggregateWindow, statistic);
    }

    private List<FluxTable> executeQuery(String query) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(query);
    }

    private void updateStats(Map<String, TimeWindowStats> statsByTime, List<FluxTable> fluxTables, String statistic) {
        for (FluxTable table : fluxTables) {
            for (FluxRecord record : table.getRecords()) {
                String time = record.getValueByKey("_time").toString();
                Double value = record.getValueByKey("_value") != null
                        ? Double.valueOf(Objects.requireNonNull(record.getValueByKey("_value")).toString())
                        : null;

                // Merge results for the same timestamp
                statsByTime.compute(time, (key, existing) -> {
                    return Objects.requireNonNullElseGet(existing, () -> new TimeWindowStats(time, null, null, null, null)).updateStat(statistic, value);
                });
            }
        }
    }

    public void saveSensorData(SensorData sensorData) {
        // Prepare the point to write
        String deviceId = sensorData.deviceId();
        double value = sensorData.value();
        Instant timestamp = sensorData.timestamp();

        // Create a point with the required fields
        Point point = Point.measurement("sensor_data")
                .addTag("deviceId", deviceId)
                .addField("value", value)
                .time(timestamp.toEpochMilli(), WritePrecision.MS);

        // Get the WriteApiBlocking instance
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        // Write the point to InfluxDB
        writeApi.writePoint(bucket, org, point);

    }
}
