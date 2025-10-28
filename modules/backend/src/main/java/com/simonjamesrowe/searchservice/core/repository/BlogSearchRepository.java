package com.simonjamesrowe.searchservice.core.repository;

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult;
import java.util.Collection;

public interface BlogSearchRepository {
    Collection<BlogSearchResult> search(String q);
    Collection<BlogSearchResult> getAll();
}