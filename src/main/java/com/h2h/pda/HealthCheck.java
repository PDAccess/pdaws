package com.h2h.pda;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HealthCheck {
    public static void main(String... args) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate build = builder.build();
        ResponseEntity<String> entity = build.getForEntity("http://localhost:8080/api/v1/system/status", String.class);
        System.exit(entity.getStatusCode() != HttpStatus.OK ? 1 : 0);
    }
}
