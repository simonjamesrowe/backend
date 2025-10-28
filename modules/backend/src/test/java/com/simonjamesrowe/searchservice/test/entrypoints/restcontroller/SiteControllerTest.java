package com.simonjamesrowe.searchservice.test.entrypoints.restcontroller;

import com.simonjamesrowe.searchservice.core.model.SiteSearchResult;
import com.simonjamesrowe.searchservice.core.usecase.SearchSiteUseCase;
import com.simonjamesrowe.searchservice.entrypoints.restcontroller.SiteController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SiteController.class)
class SiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchSiteUseCase searchSiteUseCase;

    @Test
    void searchResultsShouldBeCorrect() throws Exception {
        List<SiteSearchResult.Hit> hits1 = List.of(
            new SiteSearchResult.Hit("Hit 1", "image1.jpg", "/link1"),
            new SiteSearchResult.Hit("Hit 2", "image2.jpg", "/link2")
        );
        List<SiteSearchResult.Hit> hits2 = List.of(
            new SiteSearchResult.Hit("Hit 3", "image3.jpg", "/link3")
        );

        List<SiteSearchResult> searchResults = List.of(
            new SiteSearchResult("Blogs", hits1),
            new SiteSearchResult("Jobs", hits2)
        );

        when(searchSiteUseCase.search("Universal")).thenReturn(searchResults);

        mockMvc.perform(get("/site")
                .param("q", "Universal")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").value("Blogs"))
            .andExpect(jsonPath("$[0].hits.length()").value(2))
            .andExpect(jsonPath("$[0].hits[0].name").value("Hit 1"))
            .andExpect(jsonPath("$[0].hits[0].link").value("/link1"))
            .andExpect(jsonPath("$[0].hits[0].imageUrl").value("image1.jpg"))
            .andExpect(jsonPath("$[1].type").value("Jobs"))
            .andExpect(jsonPath("$[1].hits.length()").value(1));
    }
}