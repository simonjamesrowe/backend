package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site;

import com.simonjamesrowe.searchservice.config.ElasticSearchIndexProperties;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.AbstractIndexConfig;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.stereotype.Component;

@Component
public class SiteDocumentIndexConfig extends AbstractIndexConfig {

    private static final String SETTINGS = """
        {
          "analysis" : {
            "analyzer": {
              "lowercase_keyword":{
                "tokenizer":"keyword",
                "filter": ["lowercase"]
              },
              "markdown_text" : {
                "char_filter": [
                  "html_strip"
                ],
                "tokenizer" : "lowercase",
                "filter" : ["stop", "unique", "porter_stem"]
              }
            }
          }
        }
        """;

    private static final String MAPPINGS = """
        {
           "properties" : {
              "name" : {
                "type" : "text",
                "fields": {
                  "raw": {
                    "type":  "keyword"
                  },
                  "search": {
                    "type" : "search_as_you_type"
                  }
                }
              },
              "shortDescription" : {
                "type": "text",
                "store": false,
                "index": true,
                "analyzer": "markdown_text"
              },
              "longDescription" : {
                "type": "text",
                "store": false,
                "index": true,
                "analyzer": "markdown_text"
              },
              "siteUrl" :{
                "type" : "keyword",
                "store" : true,
                "index" : false
              },
              "image" :{
                "type" : "keyword",
                "store" : true,
                "index" : false
              },
              "type" : {
                "type" : "text",
                "fields": {
                  "keyword": {
                    "type": "keyword"
                  }
                }
              }
           }
         }
        """;

    public SiteDocumentIndexConfig(ElasticsearchClient elasticsearchClient,
                                  ElasticSearchIndexProperties elasticSearchIndexProperties) {
        super(elasticsearchClient, elasticSearchIndexProperties.site());
    }

    @Override
    public String getSettings() {
        return SETTINGS;
    }

    @Override
    public String getMappings() {
        return MAPPINGS;
    }
}