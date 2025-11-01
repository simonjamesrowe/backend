package com.simonjamesrowe.backend.entrypoints.restcontroller;

import com.simonjamesrowe.backend.core.model.BlogSearchResult;
import com.simonjamesrowe.backend.core.usecase.SearchBlogsUseCase;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@CrossOrigin
@RestController
public class BlogController {

    private final SearchBlogsUseCase searchBlogsUseCase;

    public BlogController(SearchBlogsUseCase searchBlogsUseCase) {
        this.searchBlogsUseCase = searchBlogsUseCase;
    }

    @GetMapping(value = "/blogs", params = "q")
    public Collection<BlogSearchResult> search(@RequestParam String q) {
        return searchBlogsUseCase.search(q);
    }

    @GetMapping(value = "/blogs", params = "!q")
    public Collection<BlogSearchResult> getAll() {
        return searchBlogsUseCase.getAll();
    }
}