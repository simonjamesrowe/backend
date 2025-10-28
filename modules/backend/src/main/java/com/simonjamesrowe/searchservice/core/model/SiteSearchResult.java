package com.simonjamesrowe.searchservice.core.model;

import java.util.List;

public record SiteSearchResult(
    String type,
    List<Hit> hits
) {
    public record Hit(
        String name,
        String imageUrl,
        String link
    ) {
    }
}