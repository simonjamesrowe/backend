package com.simonjamesrowe.searchservice.test.usecase;

import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.repository.BlogIndexRepository;
import com.simonjamesrowe.searchservice.core.usecase.IndexBlogUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IndexBlogUseCaseTest {

    @Mock
    private BlogIndexRepository blogIndexRepository;

    @InjectMocks
    private IndexBlogUseCase indexBlogUseCase;

    @Test
    void shouldIndexPublishedBlogs() {
        IndexBlogRequest indexBlogRequest1 = new IndexBlogRequest(
            "1", "Blog 1", "Short desc 1", "Content 1", List.of(), List.of(),
            "thumb1.jpg", "small1.jpg", "med1.jpg", LocalDate.now(), true
        );
        IndexBlogRequest indexBlogRequest2 = new IndexBlogRequest(
            "2", "Blog 2", "Short desc 2", "Content 2", List.of(), List.of(),
            "thumb2.jpg", "small2.jpg", "med2.jpg", LocalDate.now(), true
        );

        indexBlogUseCase.indexBlogs(List.of(indexBlogRequest1, indexBlogRequest2));

        verify(blogIndexRepository).indexBlogs(List.of(indexBlogRequest1, indexBlogRequest2));
        verify(blogIndexRepository, never()).deleteBlogs(any());
    }

    @Test
    void shouldDeleteBlogsThatAreNotPublished() {
        IndexBlogRequest indexBlogRequest1 = new IndexBlogRequest(
            "1", "Blog 1", "Short desc 1", "Content 1", List.of(), List.of(),
            "thumb1.jpg", "small1.jpg", "med1.jpg", LocalDate.now(), false
        );
        IndexBlogRequest indexBlogRequest2 = new IndexBlogRequest(
            "2", "Blog 2", "Short desc 2", "Content 2", List.of(), List.of(),
            "thumb2.jpg", "small2.jpg", "med2.jpg", LocalDate.now(), false
        );

        indexBlogUseCase.indexBlogs(List.of(indexBlogRequest1, indexBlogRequest2));

        verify(blogIndexRepository, never()).indexBlogs(any());
        verify(blogIndexRepository).deleteBlogs(List.of(indexBlogRequest1.id(), indexBlogRequest2.id()));
    }
}