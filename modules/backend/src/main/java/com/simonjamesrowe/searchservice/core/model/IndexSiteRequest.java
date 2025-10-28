package com.simonjamesrowe.searchservice.core.model;

public record IndexSiteRequest(
    String id,
    String siteUrl,
    String name,
    String type,
    String image,
    String shortDescription,
    String longDescription
) {
}