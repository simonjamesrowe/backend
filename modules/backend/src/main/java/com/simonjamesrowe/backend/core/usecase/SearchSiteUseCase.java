package com.simonjamesrowe.backend.core.usecase;

import com.simonjamesrowe.backend.core.model.SiteSearchResult;
import com.simonjamesrowe.backend.core.repository.SiteSearchRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchSiteUseCase {
    private final SiteSearchRepository siteSearchRepository;

    public SearchSiteUseCase(SiteSearchRepository siteSearchRepository) {
        this.siteSearchRepository = siteSearchRepository;
    }

    public List<SiteSearchResult> search(String q) {
        return siteSearchRepository.search(q);
    }

    public List<SiteSearchResult> getAll() {
        return siteSearchRepository.getAll();
    }
}