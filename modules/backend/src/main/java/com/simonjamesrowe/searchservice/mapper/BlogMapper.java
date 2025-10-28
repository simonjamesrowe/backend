package com.simonjamesrowe.searchservice.mapper;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;

import java.util.stream.Collectors;

public final class BlogMapper {

    private BlogMapper() {
        // Utility class
    }

    public static IndexBlogRequest toBlogIndexRequest(BlogResponseDTO blog) {
        String thumbnailUrl = "";
        String smallUrl = null;
        String mediumUrl = null;

        if (blog.image() != null && blog.image().formats() != null) {
            if (blog.image().formats().thumbnail() != null) {
                thumbnailUrl = blog.image().formats().thumbnail().url();
            }
            if (blog.image().formats().large() != null) {
                smallUrl = blog.image().formats().large().url();
            }
            if (blog.image().formats().medium() != null) {
                mediumUrl = blog.image().formats().medium().url();
            }
        }

        return new IndexBlogRequest(
            blog.id(),
            blog.title(),
            blog.shortDescription(),
            blog.content(),
            blog.tags().stream().map(tag -> tag.name()).collect(Collectors.toList()),
            blog.skills().stream().map(skill -> skill.name()).collect(Collectors.toList()),
            thumbnailUrl,
            smallUrl,
            mediumUrl,
            blog.createdAt().toLocalDate(),
            blog.published()
        );
    }

    public static IndexSiteRequest toSiteIndexRequest(BlogResponseDTO blog) {
        String imageUrl = "";
        if (blog.image() != null && blog.image().formats() != null
                && blog.image().formats().thumbnail() != null) {
            imageUrl = blog.image().formats().thumbnail().url();
        }

        return new IndexSiteRequest(
            "blog_" + blog.id(),
            "/blogs/" + blog.id(),
            blog.title(),
            "Blogs",
            imageUrl,
            blog.shortDescription(),
            blog.content()
        );
    }
}