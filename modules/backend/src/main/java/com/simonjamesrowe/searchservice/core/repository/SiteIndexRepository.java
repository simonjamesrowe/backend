package com.simonjamesrowe.searchservice.core.repository;

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import java.util.Collection;

public interface SiteIndexRepository {
    void indexSites(Collection<IndexSiteRequest> requests);
}