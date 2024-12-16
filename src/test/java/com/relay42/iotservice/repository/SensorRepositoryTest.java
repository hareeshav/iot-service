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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SensorRepositoryTest {

    private final String bucket = "test-bucket";
    private final String org = "test-org";
    @Mock
    private InfluxDBClient influxDBClient;
    @Mock
    private QueryApi queryApi;
    @Mock
    private WriteApiBlocking writeApi;
    private SensorRepository sensorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sensorRepository = new SensorRepository(influxDBClient);

        // Inject mock behavior
        when(influxDBClient.getQueryApi()).thenReturn(queryApi);
        when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApi);

        // Use reflection to inject @Value fields
        injectValue(sensorRepository, "bucket", bucket);
        injectValue(sensorRepository, "org", org);
    }

    @Test
    void testSaveSensorData() {
        SensorData sensorData = new SensorData("device1", 42.0, Instant.now());

        sensorRepository.saveSensorData(sensorData);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(writeApi).writePoint(eq(bucket), eq(org), pointCaptor.capture());

        Point capturedPoint = pointCaptor.getValue();
        assertEquals(WritePrecision.MS, capturedPoint.getPrecision());
        assertNotNull(capturedPoint.getTime());
    }

    @Test
    void testQuerySensorStats() {
        String deviceId = "device1";
        String start = "2024-12-14T00:00:00Z";
        String end = "2024-12-14T03:00:00Z";
        String aggregateWindow = "1h";
        List<String> statistics = List.of("min", "max");

        // Mock FluxTable and FluxRecord
        FluxTable fluxTable = mock(FluxTable.class);
        FluxRecord record = mock(FluxRecord.class);

        when(record.getValueByKey("_time")).thenReturn("2024-12-14T01:00:00Z");
        when(record.getValueByKey("_value")).thenReturn(25.0);
        when(fluxTable.getRecords()).thenReturn(List.of(record));
        when(queryApi.query(anyString())).thenReturn(List.of(fluxTable));

        List<TimeWindowStats> stats = sensorRepository.querySensorStats(deviceId, start, end, aggregateWindow, statistics);

        assertEquals(1, stats.size());
        TimeWindowStats stat = stats.get(0);
        assertEquals("2024-12-14T01:00:00Z", stat.time());
        assertNotNull(stat.min());
        assertNotNull(stat.max());
        assertNull(stat.median());
        assertNull(stat.mean());
    }

    private void injectValue(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
