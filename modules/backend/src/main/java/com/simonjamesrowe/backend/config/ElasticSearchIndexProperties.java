package com.simonjamesrowe.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("elasticsearch.index")
public record ElasticSearchIndexProperties(
    String blog,
    String site
) {
}