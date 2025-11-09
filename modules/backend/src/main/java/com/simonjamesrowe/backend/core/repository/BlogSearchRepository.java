package com.simonjamesrowe.backend.core.repository;

import com.simonjamesrowe.backend.core.model.BlogSearchResult;
import java.util.Collection;

public interface BlogSearchRepository {
    Collection<BlogSearchResult> search(String q);
    Collection<BlogSearchResult> getAll();
}