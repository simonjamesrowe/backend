package com.simonjamesrowe.backend.core.usecase;

import com.simonjamesrowe.backend.core.model.IndexSiteRequest;
import com.simonjamesrowe.backend.core.repository.SiteIndexRepository;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class IndexSiteUseCase {
    private final SiteIndexRepository siteIndexRepository;

    public IndexSiteUseCase(SiteIndexRepository siteIndexRepository) {
        this.siteIndexRepository = siteIndexRepository;
    }

    public void indexSites(Collection<IndexSiteRequest> requests) {
        siteIndexRepository.indexSites(requests);
    }
}