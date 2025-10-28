package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BlogDocumentRepository extends ElasticsearchRepository<BlogDocument, String> {

    @Query("""
        {
          "bool": {
            "should": [
              {
                "function_score": {
                  "query": {
                    "multi_match": {
                      "query": "?0",
                      "type": "bool_prefix",
                      "fields": [
                        "title"
                      ]
                    }
                  },
                  "boost": 5
                }
              },
              {
                "multi_match": {
                  "query": "?0",
                  "fields": [
                    "title",
                    "content",
                    "shortDescription",
                    "skills^2",
                    "tags^2"
                  ]
                }
              }
            ]
          }
        }
        """)
    Collection<BlogDocument> getBlogsByQuery(String q);

    @Query("""
        {
          "bool": {
            "must": {
              "match_all": {}
            },
            "filter": [
              { "term" : { "tags" : "?0"}}
            ]
          }
        }
        """)
    List<BlogDocument> getBlogsByTag(String tag, Pageable pageable);
}