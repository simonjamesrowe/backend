package com.simonjamesrowe.searchservice.entrypoints.restcontroller;

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class SiteController {

    private final SearchSiteUseCase searchSiteUseCase;

    public SiteController(SearchSiteUseCase searchSiteUseCase) {
        this.searchSiteUseCase = searchSiteUseCase;
    }

    @GetMapping("/site")
    public List<SiteSearchResult> siteSearch(@RequestParam String q) {
        return searchSiteUseCase.search(q);
    }
}