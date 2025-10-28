package com.simonjamesrowe.searchservice.core.usecase;

import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.repository.BlogIndexRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class IndexBlogUseCase {
    private final BlogIndexRepository blogIndexRepository;

    public IndexBlogUseCase(BlogIndexRepository blogIndexRepository) {
        this.blogIndexRepository = blogIndexRepository;
    }

    public void indexBlogs(Collection<IndexBlogRequest> indexBlogRequests) {
        boolean hasPublished = indexBlogRequests.stream().anyMatch(IndexBlogRequest::published);
        boolean hasUnpublished = indexBlogRequests.stream().anyMatch(request -> !request.published());

        if (hasPublished) {
            Collection<IndexBlogRequest> publishedRequests = indexBlogRequests.stream()
                .filter(IndexBlogRequest::published)
                .collect(Collectors.toList());
            blogIndexRepository.indexBlogs(publishedRequests);
        }

        if (hasUnpublished) {
            Collection<String> unpublishedIds = indexBlogRequests.stream()
                .filter(request -> !request.published())
                .map(IndexBlogRequest::id)
                .collect(Collectors.toList());
            blogIndexRepository.deleteBlogs(unpublishedIds);
        }
    }
}