package com.simonjamesrowe.searchservice.test.entrypoints.restcontroller;

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult;
import com.simonjamesrowe.searchservice.core.usecase.SearchBlogsUseCase;
import com.simonjamesrowe.searchservice.entrypoints.restcontroller.BlogController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlogController.class)
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchBlogsUseCase searchBlogsUseCase;

    @Test
    void searchShouldReturnExpectedResults() throws Exception {
        List<BlogSearchResult> searchResults = List.of(
            new BlogSearchResult("1", "Blog 1", "Short desc 1", List.of("tag1"),
                "thumb1.jpg", "small1.jpg", "med1.jpg", LocalDate.of(2023, 1, 1)),
            new BlogSearchResult("2", "Blog 2", "Short desc 2", List.of("tag2"),
                "thumb2.jpg", "small2.jpg", "med2.jpg", LocalDate.of(2023, 1, 2)),
            new BlogSearchResult("3", "Blog 3", "Short desc 3", List.of("tag3"),
                "thumb3.jpg", "small3.jpg", "med3.jpg", LocalDate.of(2023, 1, 3))
        );

        when(searchBlogsUseCase.search("kotlin")).thenReturn(searchResults);

        mockMvc.perform(get("/blogs")
                .param("q", "kotlin")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].title").value("Blog 1"))
            .andExpect(jsonPath("$[0].shortDescription").value("Short desc 1"))
            .andExpect(jsonPath("$[0].thumbnailImage").value("thumb1.jpg"))
            .andExpect(jsonPath("$[0].createdDate").value("2023-01-01"));
    }

    @Test
    void getAllShouldReturnExpectedResults() throws Exception {
        List<BlogSearchResult> searchResults = List.of(
            new BlogSearchResult("1", "Blog 1", "Short desc 1", List.of("tag1"),
                "thumb1.jpg", "small1.jpg", "med1.jpg", LocalDate.of(2023, 1, 1)),
            new BlogSearchResult("2", "Blog 2", "Short desc 2", List.of("tag2"),
                "thumb2.jpg", "small2.jpg", "med2.jpg", LocalDate.of(2023, 1, 2)),
            new BlogSearchResult("3", "Blog 3", "Short desc 3", List.of("tag3"),
                "thumb3.jpg", "small3.jpg", "med3.jpg", LocalDate.of(2023, 1, 3))
        );

        when(searchBlogsUseCase.getAll()).thenReturn(searchResults);

        mockMvc.perform(get("/blogs")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].title").value("Blog 1"))
            .andExpect(jsonPath("$[0].shortDescription").value("Short desc 1"))
            .andExpect(jsonPath("$[0].thumbnailImage").value("thumb1.jpg"))
            .andExpect(jsonPath("$[0].createdDate").value("2023-01-01"));
    }
}