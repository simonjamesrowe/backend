package com.simonjamesrowe.searchservice.test.usecase;

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult;
import com.simonjamesrowe.searchservice.core.repository.BlogSearchRepository;
import com.simonjamesrowe.searchservice.core.usecase.SearchBlogsUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchBlogsUseCaseTest {

    @InjectMocks
    private SearchBlogsUseCase searchBlogUseCase;

    @Mock
    private BlogSearchRepository blogSearchRepository;

    @Test
    void shouldSearchForBlogsWithQuery() {
        BlogSearchResult blogSearchResult1 = new BlogSearchResult(
            "1", "Title 1", "Description 1", List.of("tag1"),
            "thumb1.jpg", "small1.jpg", "medium1.jpg", java.time.LocalDate.now()
        );
        BlogSearchResult blogSearchResult2 = new BlogSearchResult(
            "2", "Title 2", "Description 2", List.of("tag2"),
            "thumb2.jpg", "small2.jpg", "medium2.jpg", java.time.LocalDate.now()
        );
        List<BlogSearchResult> expectedResults = Arrays.asList(blogSearchResult1, blogSearchResult2);

        when(blogSearchRepository.search("kotlin"))
            .thenReturn(expectedResults);

        Collection<BlogSearchResult> actualResults = searchBlogUseCase.search("kotlin");

        Assertions.assertThat(actualResults).isEqualTo(expectedResults);
    }

    @Test
    void shouldReturnAllBlogs() {
        BlogSearchResult blogSearchResult1 = new BlogSearchResult(
            "1", "Title 1", "Description 1", List.of("tag1"),
            "thumb1.jpg", "small1.jpg", "medium1.jpg", java.time.LocalDate.now()
        );
        BlogSearchResult blogSearchResult2 = new BlogSearchResult(
            "2", "Title 2", "Description 2", List.of("tag2"),
            "thumb2.jpg", "small2.jpg", "medium2.jpg", java.time.LocalDate.now()
        );
        List<BlogSearchResult> expectedResults = Arrays.asList(blogSearchResult1, blogSearchResult2);

        when(blogSearchRepository.getAll())
            .thenReturn(expectedResults);

        Collection<BlogSearchResult> actualResults = searchBlogUseCase.getAll();

        Assertions.assertThat(actualResults).isEqualTo(expectedResults);
    }
}