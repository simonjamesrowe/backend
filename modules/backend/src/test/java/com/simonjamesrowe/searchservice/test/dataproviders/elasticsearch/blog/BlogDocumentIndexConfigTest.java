package com.simonjamesrowe.searchservice.test.dataproviders.elasticsearch.blog;

import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer;
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig;
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocumentIndexConfig;
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
@Import({BlogDocumentIndexConfig.class, ElasticSearchConfig.class})
class BlogDocumentIndexConfigTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    void blogIndexShouldHaveExpectedMappingsAndSettings() {
        var indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of("blog_local"));
        Map<String, Object> mapping = indexOps.getMapping();
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");

        Assertions.assertThat(properties.get("skills")).isEqualTo(
            Map.of(
                "analyzer", "lowercase_keyword",
                "store", true,
                "type", "text"
            )
        );
        Assertions.assertThat(properties.get("shortDescription")).isEqualTo(
            Map.of(
                "store", true,
                "type", "text"
            )
        );
        Assertions.assertThat(properties.get("tags")).isEqualTo(
            Map.of(
                "analyzer", "lowercase_keyword",
                "store", true,
                "type", "text"
            )
        );
        Assertions.assertThat(properties.get("createdDate")).isEqualTo(
            Map.of(
                "format", "uuuu-MM-dd",
                "store", true,
                "type", "date"
            )
        );
        Assertions.assertThat(properties.get("smallImage")).isEqualTo(
            Map.of(
                "index", false,
                "store", true,
                "type", "keyword"
            )
        );
        Assertions.assertThat(properties.get("mediumImage")).isEqualTo(
            Map.of(
                "index", false,
                "store", true,
                "type", "keyword"
            )
        );
        Assertions.assertThat(properties.get("thumbnailImage")).isEqualTo(
            Map.of(
                "index", false,
                "store", true,
                "type", "keyword"
            )
        );
        Assertions.assertThat(properties.get("title")).isEqualTo(
            Map.of(
                "store", true,
                "max_shingle_size", 3,
                "type", "search_as_you_type"
            )
        );
        Assertions.assertThat(properties.get("content")).isEqualTo(
            Map.of(
                "analyzer", "markdown_text",
                "type", "text"
            )
        );
    }
}