package com.simonjamesrowe.searchservice.core.usecase;

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import com.simonjamesrowe.searchservice.core.repository.SiteSearchRepository;
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