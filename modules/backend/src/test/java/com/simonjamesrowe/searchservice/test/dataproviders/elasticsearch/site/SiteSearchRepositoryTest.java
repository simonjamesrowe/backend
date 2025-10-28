package com.simonjamesrowe.searchservice.test.dataproviders.elasticsearch.site;

import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer;
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig;
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteDocumentIndexConfig;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteDocumentRepository;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteSearchRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@WithElasticsearchContainer
@JsonTest
@ImportAutoConfiguration({
    ElasticsearchDataAutoConfiguration.class,
    ElasticsearchRepositoriesAutoConfiguration.class,
    ElasticsearchRestClientAutoConfiguration.class
})
@EnableConfigurationProperties(ElasticSearchIndexProperties.class)
@Import({SiteDocumentIndexConfig.class, SiteSearchRepository.class, ElasticSearchConfig.class})
class SiteSearchRepositoryTest {

    @Autowired
    private SiteSearchRepository siteSearchRepository;

    @Autowired
    private SiteDocumentRepository siteDocumentRepository;

    @BeforeEach
    void setupData() {
        siteDocumentRepository.deleteAll();
        List<IndexSiteRequest> indexRequests = List.of(
            new IndexSiteRequest(
                "blog_1",
                "/blogs/1",
                "My first Blog",
                "Blog",
                "http://image1",
                "jenkins",
                ""
            ),
            new IndexSiteRequest(
                "blog_2",
                "/blogs/2",
                "My second Blog",
                "Blog",
                "http://image2",
                "spring",
                ""
            ),
            new IndexSiteRequest(
                "jobs_1",
                "/jobs/1",
                "Senior Developer (Some Company)",
                "Job",
                "http://image3",
                "spring",
                ""
            ),
            new IndexSiteRequest(
                "skills_1",
                "skills/1",
                "Spring Boot",
                "Skill",
                "http://image4",
                "spring boot",
                ""
            )
        );
        siteSearchRepository.indexSites(indexRequests);
    }

    @Test
    void shouldReturnRelevantResults() {
        List<SiteSearchResult> results = siteSearchRepository.search("spring");

        Assertions.assertThat(results).hasSize(3);
        Assertions.assertThat(results.get(0).type()).isEqualTo("Blog");
        Assertions.assertThat(results.get(1).type()).isEqualTo("Job");
        Assertions.assertThat(results.get(2).type()).isEqualTo("Skill");
    }
}