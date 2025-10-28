package com.simonjamesrowe.searchservice.test.usecase;

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import com.simonjamesrowe.searchservice.core.repository.SiteSearchRepository;
import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchSiteUseCaseTest {

    @InjectMocks
    private SearchSiteUseCase searchSiteUseCase;

    @Mock
    private SiteSearchRepository siteSearchRepository;

    @Test
    void shouldReturnSiteSearchResults() {
        List<SiteSearchResult.Hit> hits1 = List.of(
            new SiteSearchResult.Hit("Java Developer", "company1.jpg", "/jobs/1")
        );
        List<SiteSearchResult.Hit> hits2 = List.of(
            new SiteSearchResult.Hit("Kotlin Blog", "blog1.jpg", "/blogs/1")
        );

        SiteSearchResult result1 = new SiteSearchResult("Job", hits1);
        SiteSearchResult result2 = new SiteSearchResult("Blog", hits2);

        when(siteSearchRepository.search("kotlin")).thenReturn(List.of(result1, result2));

        List<SiteSearchResult> results = searchSiteUseCase.search("kotlin");

        Assertions.assertThat(results).isEqualTo(List.of(result1, result2));
    }
}