package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Document(indexName = "#{@blogIndexName}", createIndex = false)
public class BlogDocument {

    @Id
    private String id;

    @Field
    private String title;

    @Field
    private String shortDescription;

    @Field
    @JsonIgnore
    private String content;

    @Field
    private List<String> tags;

    @Field
    private List<String> skills;

    @Field
    private String thumbnailImage;

    @Field
    private String smallImage;

    @Field
    private String mediumImage;

    @Field(
        type = FieldType.Date,
        format = {DateFormat.date},
        pattern = {"uuuu-MM-dd"}
    )
    private LocalDate createdDate;

    public BlogDocument() {
    }

    public BlogDocument(String id, String title, String shortDescription, String content,
                       List<String> tags, List<String> skills, String thumbnailImage,
                       String smallImage, String mediumImage, LocalDate createdDate) {

        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.content = content;
        this.tags = tags;
        this.skills = skills;
        this.thumbnailImage = thumbnailImage;
        this.smallImage = smallImage;
        this.mediumImage = mediumImage;
        this.createdDate = createdDate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getMediumImage() {
        return mediumImage;
    }

    public void setMediumImage(String mediumImage) {
        this.mediumImage = mediumImage;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
}