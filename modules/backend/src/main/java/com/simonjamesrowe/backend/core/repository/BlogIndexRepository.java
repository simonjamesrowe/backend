package com.simonjamesrowe.backend.core.repository;

import com.simonjamesrowe.backend.core.model.IndexBlogRequest;
import java.util.Collection;

public interface BlogIndexRepository {

    void indexBlog(IndexBlogRequest request);

    void indexBlogs(Collection<IndexBlogRequest> requests);

    void deleteBlog(String id);

    void deleteBlogs(Collection<String> ids);
}