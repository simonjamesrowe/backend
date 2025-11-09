package com.simonjamesrowe.backend.core.repository;

import com.simonjamesrowe.backend.core.model.IndexSiteRequest;
import java.util.Collection;

public interface SiteIndexRepository {
    void indexSites(Collection<IndexSiteRequest> requests);
}