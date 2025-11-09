package com.simonjamesrowe.backend.core.repository;

import com.simonjamesrowe.backend.core.model.SiteSearchResult;
import java.util.List;

public interface SiteSearchRepository {

    List<SiteSearchResult> search(String q);

    List<SiteSearchResult> getAll();
}