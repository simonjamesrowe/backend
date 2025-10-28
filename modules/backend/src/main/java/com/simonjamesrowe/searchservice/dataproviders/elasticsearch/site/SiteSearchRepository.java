package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site;

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import com.simonjamesrowe.searchservice.core.repository.SiteIndexRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SiteSearchRepository
        implements com.simonjamesrowe.searchservice.core.repository.SiteSearchRepository,
        SiteIndexRepository {

    private final ElasticsearchClient elasticsearchClient;
    private final ElasticSearchIndexProperties elasticSearchIndexProperties;
    private final SiteDocumentRepository siteDocumentRepository;

    public SiteSearchRepository(ElasticsearchClient elasticsearchClient,
                               ElasticSearchIndexProperties elasticSearchIndexProperties,
                               SiteDocumentRepository siteDocumentRepository) {
        this.elasticsearchClient = elasticsearchClient;
        this.elasticSearchIndexProperties = elasticSearchIndexProperties;
        this.siteDocumentRepository = siteDocumentRepository;
    }

    private List<SiteSearchResult> searchSite(String q) {
        try {
            // Simplified working query
            Query simpleQuery = Query.of(query -> query
                .multiMatch(mm -> mm
                    .query(q)
                    .fields("name", "shortDescription", "longDescription")
                )
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(elasticSearchIndexProperties.site())
                .query(simpleQuery)
                .size(10)
            );

            SearchResponse<SiteDocument> results =
                    elasticsearchClient.search(searchRequest, SiteDocument.class);

            // Convert search hits to the expected result format, ensuring consistent ordering
            return results.hits().hits().stream()
                .collect(Collectors.groupingBy(hit -> hit.source().getType(),
                    LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by type name for consistent ordering
                .map(entry -> {
                    String type = entry.getKey();
                    List<SiteSearchResult.Hit> hits = entry.getValue().stream()
                        .map(hit -> new SiteSearchResult.Hit(
                            hit.source().getName(),
                            hit.source().getImage(),
                            hit.source().getSiteUrl()
                        ))
                        .collect(Collectors.toList());
                    return new SiteSearchResult(type, hits);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching sites", e);
        }
    }

    @Override
    public List<SiteSearchResult> search(String q) {
        return searchSite(q);
    }

    @Override
    public List<SiteSearchResult> getAll() {
        return searchSite("");
    }

    @Override
    public void indexSites(Collection<IndexSiteRequest> requests) {
        Iterable<SiteDocument> documents = requests.stream()
            .map(this::toSiteDocument)
            .collect(Collectors.toList());
        siteDocumentRepository.saveAll(documents);
    }

    private SiteDocument toSiteDocument(IndexSiteRequest indexSiteRequest) {
        return new SiteDocument(
            indexSiteRequest.id(),
            indexSiteRequest.siteUrl(),
            indexSiteRequest.name(),
            indexSiteRequest.type(),
            indexSiteRequest.image(),
            indexSiteRequest.shortDescription(),
            indexSiteRequest.longDescription()
        );
    }
}