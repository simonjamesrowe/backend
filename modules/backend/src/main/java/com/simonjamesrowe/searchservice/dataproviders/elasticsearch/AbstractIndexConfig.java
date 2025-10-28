package com.simonjamesrowe.searchservice.dataproviders.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.OpenRequest;
import co.elastic.clients.elasticsearch.indices.CloseIndexRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsRequest;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.StringReader;

public abstract class AbstractIndexConfig implements ApplicationRunner {

    private final ElasticsearchClient elasticsearchClient;
    private final String indexName;

    protected AbstractIndexConfig(ElasticsearchClient elasticsearchClient, String indexName) {
        this.elasticsearchClient = elasticsearchClient;
        this.indexName = indexName;
    }

    public abstract String getSettings();
    public abstract String getMappings();

    @Override
    public void run(ApplicationArguments args) {
        try {
            closeIndex();
        } catch (Exception ignored) {
            // Index might not exist yet
        }
        createOrUpdateIndex();
        openIndex();
    }

    private void createOrUpdateIndex() {
        String body = String.format("""
            {
              "settings" : %s,
              "mappings" : %s
            }
            """, getSettings(), getMappings());

        try {
            CreateIndexRequest createRequest = CreateIndexRequest.of(c -> c
                .index(indexName)
                .withJson(new StringReader(body))
            );
            elasticsearchClient.indices().create(createRequest);
        } catch (Exception e) {
            try {
                PutIndicesSettingsRequest settingsRequest = PutIndicesSettingsRequest.of(s -> s
                    .index(indexName)
                    .withJson(new StringReader(getSettings()))
                );
                elasticsearchClient.indices().putSettings(settingsRequest);

                PutMappingRequest mappingsRequest = PutMappingRequest.of(m -> m
                    .index(indexName)
                    .withJson(new StringReader(getMappings()))
                );
                elasticsearchClient.indices().putMapping(mappingsRequest);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create or update index", ex);
            }
        }
    }

    private void openIndex() {
        try {
            OpenRequest request = OpenRequest.of(o -> o.index(indexName));
            elasticsearchClient.indices().open(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open index", e);
        }
    }

    private void closeIndex() {
        try {
            CloseIndexRequest request = CloseIndexRequest.of(c -> c.index(indexName));
            elasticsearchClient.indices().close(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to close index", e);
        }
    }
}