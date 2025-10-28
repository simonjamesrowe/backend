package com.simonjamesrowe.searchservice.test.usecase;

import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.core.usecase.IndexSiteUseCase;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.site.SiteSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IndexSiteUseCaseTest {

    @Mock
    private SiteSearchRepository siteSearchRepository;

    @InjectMocks
    private IndexSiteUseCase indexSiteUseCase;

    @Test
    void shouldIndexSiteRequests() {
        IndexSiteRequest indexSiteRequest1 = new IndexSiteRequest(
            "site1", "/site1", "Site 1", "Blog", "image1.jpg", "Short desc 1", "Long desc 1"
        );
        IndexSiteRequest indexSiteRequest2 = new IndexSiteRequest(
            "site2", "/site2", "Site 2", "Job", "image2.jpg", "Short desc 2", "Long desc 2"
        );

        indexSiteUseCase.indexSites(List.of(indexSiteRequest1, indexSiteRequest2));

        verify(siteSearchRepository).indexSites(List.of(indexSiteRequest1, indexSiteRequest2));
    }
}