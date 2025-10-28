package com.simonjamesrowe.searchservice.test.dataproviders.elasticsearch.site;

import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer;
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig;
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteDocumentIndexConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.Map;

@WithElasticsearchContainer
@JsonTest
@ImportAutoConfiguration({
    ElasticsearchDataAutoConfiguration.class,
    ElasticsearchRepositoriesAutoConfiguration.class,
    ElasticsearchRestClientAutoConfiguration.class
})
@EnableConfigurationProperties(ElasticSearchIndexProperties.class)
@Import({SiteDocumentIndexConfig.class, ElasticSearchConfig.class})
class SiteDocumentIndexConfigTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    void siteIndexShouldHaveExpectedMappingsAndSettings() {
        var indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of("site_local"));
        Map<String, Object> mapping = indexOps.getMapping();
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");

        Assertions.assertThat(properties.get("name")).isEqualTo(
            Map.of(
                "type", "text",
                "fields", Map.of(
                    "search", Map.of(
                        "max_shingle_size", 3,
                        "type", "search_as_you_type"
                    ),
                    "raw", Map.of(
                        "type", "keyword"
                    )
                )
            )
        );
        Assertions.assertThat(properties.get("siteUrl")).isEqualTo(
            Map.of(
                "index", false,
                "store", true,
                "type", "keyword"
            )
        );
        Assertions.assertThat(properties.get("image")).isEqualTo(
            Map.of(
                "index", false,
                "store", true,
                "type", "keyword"
            )
        );
        Assertions.assertThat(properties.get("shortDescription")).isEqualTo(
            Map.of(
                "analyzer", "markdown_text",
                "type", "text"
            )
        );
        Assertions.assertThat(properties.get("longDescription")).isEqualTo(
            Map.of(
                "analyzer", "markdown_text",
                "type", "text"
            )
        );
    }
}