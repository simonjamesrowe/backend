package com.simonjamesrowe.searchservice.core.repository;

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import java.util.List;

public interface SiteSearchRepository {

    List<SiteSearchResult> search(String q);

    List<SiteSearchResult> getAll();
}