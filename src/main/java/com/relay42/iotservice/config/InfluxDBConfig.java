package com.relay42.iotservice.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Value("${influxdb.url}")
    private String influxDbUrl;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String orgName;

    @Value("${influxdb.bucket}")
    private String bucketName;

    private InfluxDBClient influxDBClient;

    @PostConstruct
    public void init() {
        influxDBClient = InfluxDBClientFactory.create(influxDbUrl, token.toCharArray(), orgName, bucketName);

        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        if (influxDBClient.getBucketsApi().findBuckets().stream()
                .noneMatch(bucket -> bucket.getName().equals(bucketName))) {
            influxDBClient.getBucketsApi().createBucket(bucketName, orgName);
        }
    }

    @Bean
    public InfluxDBClient influxDBClient() {
        return influxDBClient;
    }
}
