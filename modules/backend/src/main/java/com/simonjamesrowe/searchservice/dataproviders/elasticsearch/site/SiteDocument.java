package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "#{@siteIndexName}", createIndex = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteDocument {

    @Id
    private String id;

    @Field
    private String siteUrl;

    @Field
    private String name;

    @Field
    private String type;

    @Field
    private String image;

    @Field
    private String shortDescription;

    @Field
    private String longDescription;

    public SiteDocument() {
    }

    public SiteDocument(String id, String siteUrl, String name, String type,
                       String image, String shortDescription, String longDescription) {

        this.id = id;
        this.siteUrl = siteUrl;
        this.name = name;
        this.type = type;
        this.image = image;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}