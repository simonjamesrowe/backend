package com.simonjamesrowe.backend.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.cors")
public record SecurityCorsProperties(List<String> allowedOrigins) {

    public SecurityCorsProperties {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            throw new IllegalArgumentException("security.cors.allowed-origins must not be empty");
        }
    }
}
