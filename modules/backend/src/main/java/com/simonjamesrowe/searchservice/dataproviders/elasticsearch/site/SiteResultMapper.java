package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site;

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;

import java.util.List;
import java.util.stream.Collectors;

public final class SiteResultMapper {

    private SiteResultMapper() {
        // Utility class
    }

    public static List<SiteSearchResult> mapList(SearchResponse<Void> results) {
        StringTermsAggregate typeAggregation = results.aggregations()
            .get("type")
            .sterms();

        return typeAggregation.buckets().array().stream()
            .map(bucket -> {
                StringTermsAggregate idAggregation = bucket.aggregations()
                    .get("id")
                    .sterms();

                List<SiteSearchResult.Hit> hits = idAggregation.buckets().array().stream()
                    .map(idBucket -> {
                        StringTermsAggregate imageUrlAgg = idBucket.aggregations()
                            .get("imageUrl")
                            .sterms();
                        StringTermsAggregate nameAgg = idBucket.aggregations()
                            .get("name")
                            .sterms();
                        StringTermsAggregate urlAgg = idBucket.aggregations()
                            .get("url")
                            .sterms();

                        return new SiteSearchResult.Hit(
                            nameAgg.buckets().array().get(0).key().stringValue(),
                            imageUrlAgg.buckets().array().get(0).key().stringValue(),
                            urlAgg.buckets().array().get(0).key().stringValue()
                        );
                    })
                    .collect(Collectors.toList());

                return new SiteSearchResult(bucket.key().stringValue(), hits);
            })
            .collect(Collectors.toList());
    }
}