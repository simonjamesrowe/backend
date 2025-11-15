package com.simonjamesrowe.backend.entrypoints.restcontroller;

import com.simonjamesrowe.backend.core.model.SiteSearchResult;
import com.simonjamesrowe.backend.core.usecase.SearchSiteUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SiteController {

    private final SearchSiteUseCase searchSiteUseCase;

    public SiteController(SearchSiteUseCase searchSiteUseCase) {
        this.searchSiteUseCase = searchSiteUseCase;
    }

    @GetMapping({"/site", "/search/site"})
    public List<SiteSearchResult> siteSearch(@RequestParam String q) {
        return searchSiteUseCase.search(q);
    }
}
