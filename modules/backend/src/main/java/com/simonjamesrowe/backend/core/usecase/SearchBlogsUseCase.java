package com.simonjamesrowe.backend.core.usecase;

import com.simonjamesrowe.backend.core.model.BlogSearchResult;
import com.simonjamesrowe.backend.core.repository.BlogSearchRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class SearchBlogsUseCase {
    private final BlogSearchRepository blogSearchRepository;

    public SearchBlogsUseCase(BlogSearchRepository blogSearchRepository) {
        this.blogSearchRepository = blogSearchRepository;
    }

    public Collection<BlogSearchResult> search(String q) {
        return blogSearchRepository.search(q);
    }

    public Collection<BlogSearchResult> getAll() {
        return blogSearchRepository.getAll();
    }
}