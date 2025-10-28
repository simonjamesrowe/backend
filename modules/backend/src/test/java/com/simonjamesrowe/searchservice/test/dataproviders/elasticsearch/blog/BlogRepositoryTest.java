package com.simonjamesrowe.searchservice.test.dataproviders.elasticsearch.blog;

import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer;
import com.simonjamesrowe.searchservice.config.ElasticSearchConfig;
import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocumentIndexConfig;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocumentRepository;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogRepository;
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

import java.time.LocalDate;
import java.util.List;

@WithElasticsearchContainer
@JsonTest
@ImportAutoConfiguration({
    ElasticsearchDataAutoConfiguration.class,
    ElasticsearchRepositoriesAutoConfiguration.class,
    ElasticsearchRestClientAutoConfiguration.class
})
@EnableConfigurationProperties(ElasticSearchIndexProperties.class)
@Import({BlogDocumentIndexConfig.class, BlogRepository.class, ElasticSearchConfig.class})
class BlogRepositoryTest {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogDocumentRepository blogDocumentRepository;

    @BeforeEach
    void setupData() {
        blogDocumentRepository.deleteAll();
        IndexBlogRequest blog1 = new IndexBlogRequest(
            "1",
            "My first blog on kotlin",
            "Short description 1",
            "This contains <b> test on </b> kotlin jvm",
            List.of("kotlin", "jvm"),
            List.of("kotlin", "spring"),
            "/uploads/blog2-thumb.jpg",
            "/uploads/blog1-small.jpg",
            "/uploads/blog1-med.jpg",
            LocalDate.of(2021, 1, 1),
            false
        );
        IndexBlogRequest blog2 = new IndexBlogRequest(
            "2",
            "Pact for contract testing",
            "shortDescription2",
            "This contains <b> test on </b> pact spring contract tests broker with kotlin",
            List.of("pact", "jvm", "contract-testing"),
            List.of("junit", "jenkins", "spring", "kotlin"),
            "/uploads/blog2-thumb.jpg",
            "/uploads/blog2-small.jpg",
            "/uploads/blog2-med.jpg",
            LocalDate.of(2021, 1, 2),
            true
        );
        IndexBlogRequest blog3 = new IndexBlogRequest(
            "3",
            "Continuous Integration with Jenkins X",
            "shortDescription3",
            "Jenkins X is the best, uses helm and docker and kubernetes",
            List.of("jenkins", "ci", "cd"),
            List.of("helm", "jenkins", "kubernetes", "docker"),
            "/uploads/blog3-thumb.jpg",
            "/uploads/blog3-small.jpg",
            "/uploads/blog3-med.jpg",
            LocalDate.of(2021, 1, 3),
            true
        );
        blogRepository.indexBlogs(List.of(blog1, blog2, blog3));
    }

    @Test
    void shouldReturnAllBlogs() {
        Assertions.assertThat(blogRepository.getAll()).hasSize(3);
    }

    @Test
    void shouldReturnCorrectSearchResults() {
        Assertions.assertThat(blogRepository.search("kotlin")).hasSize(2);
    }

    @Test
    void shouldDeleteDocument() {
        blogRepository.deleteBlog("2");
        Assertions.assertThat(blogRepository.getAll()).hasSize(2);
    }

    @Test
    void shouldDeleteDocuments() {
        blogRepository.deleteBlogs(List.of("1", "3"));
        Assertions.assertThat(blogRepository.getAll()).hasSize(1);
    }
}