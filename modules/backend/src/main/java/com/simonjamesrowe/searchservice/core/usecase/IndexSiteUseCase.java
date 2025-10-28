package com.simonjamesrowe.searchservice.core.usecase;

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.core.repository.SiteIndexRepository;
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