package com.simonjamesrowe.searchservice.core.model;

import java.time.LocalDate;
import java.util.List;

public record BlogSearchResult(
    String id,
    String title,
    String shortDescription,
    List<String> tags,
    String thumbnailImage,
    String smallImage,
    String mediumImage,
    LocalDate createdDate
) {
}