package com.simonjamesrowe.searchservice.core.model;

import java.time.LocalDate;
import java.util.List;

public record IndexBlogRequest(
    String id,
    String title,
    String shortDescription,
    String content,
    List<String> tags,
    List<String> skills,
    String thumbnailImage,
    String smallImage,
    String mediumImage,
    LocalDate createdDate,
    Boolean published
) {
}