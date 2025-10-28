package com.simonjamesrowe.searchservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host:localhost}:${elasticsearch.port:9200}")
    private String elasticsearchHostPort;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchHostPort)
                .build();
    }

    @Bean
    public String blogIndexName(ElasticSearchIndexProperties elasticSearchIndexProperties) {
        return elasticSearchIndexProperties.blog();
    }

    @Bean
    public String siteIndexName(ElasticSearchIndexProperties elasticSearchIndexProperties) {
        return elasticSearchIndexProperties.site();
    }

}