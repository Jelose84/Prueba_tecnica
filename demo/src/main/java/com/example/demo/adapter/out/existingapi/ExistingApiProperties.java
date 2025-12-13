package com.example.demo.adapter.out.existingapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "existing-api")
public record ExistingApiProperties(
    String baseUrl,
    int connectTimeoutMs,
    int readTimeoutMs,
    int parallelism
) {}