package com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog;

import com.simonjamesrowe.searchservice.core.model.BlogSearchResult;
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.repository.BlogIndexRepository;
import com.simonjamesrowe.searchservice.core.repository.BlogSearchRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BlogRepository implements BlogSearchRepository, BlogIndexRepository {

    private final BlogDocumentRepository blogDocumentRepository;

    public BlogRepository(BlogDocumentRepository blogDocumentRepository) {
        this.blogDocumentRepository = blogDocumentRepository;
    }

    @Override
    public Collection<BlogSearchResult> search(String q) {
        return blogDocumentRepository.getBlogsByQuery(q).stream()
            .map(this::toBlogSearchResult)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<BlogSearchResult> getAll() {
        Iterable<BlogDocument> documents = blogDocumentRepository.findAll(
            Sort.by(Sort.Direction.DESC, "createdDate")
        );
        return StreamSupport.stream(documents.spliterator(), false)
            .map(this::toBlogSearchResult)
            .collect(Collectors.toList());
    }

    @Override
    public void indexBlog(IndexBlogRequest request) {
        blogDocumentRepository.save(toBlogDocument(request));
    }

    @Override
    public void indexBlogs(Collection<IndexBlogRequest> requests) {
        Iterable<BlogDocument> documents = requests.stream()
            .map(this::toBlogDocument)
            .collect(Collectors.toList());
        blogDocumentRepository.saveAll(documents);
    }

    @Override
    public void deleteBlog(String id) {
        blogDocumentRepository.findById(id).ifPresent(blogDocumentRepository::delete);
    }

    @Override
    public void deleteBlogs(Collection<String> ids) {
        Iterable<BlogDocument> documents = blogDocumentRepository.findAllById(ids);
        blogDocumentRepository.deleteAll(documents);
    }

    private BlogSearchResult toBlogSearchResult(BlogDocument blogDocument) {
        return new BlogSearchResult(
            blogDocument.getId(),
            blogDocument.getTitle(),
            blogDocument.getShortDescription(),
            blogDocument.getTags(),
            blogDocument.getThumbnailImage(),
            blogDocument.getSmallImage(),
            blogDocument.getMediumImage(),
            blogDocument.getCreatedDate()
        );
    }

    private BlogDocument toBlogDocument(IndexBlogRequest indexBlogRequest) {
        return new BlogDocument(
            indexBlogRequest.id(),
            indexBlogRequest.title(),
            indexBlogRequest.shortDescription(),
            indexBlogRequest.content(),
            indexBlogRequest.tags(),
            indexBlogRequest.skills(),
            indexBlogRequest.thumbnailImage(),
            indexBlogRequest.smallImage(),
            indexBlogRequest.mediumImage(),
            indexBlogRequest.createdDate()
        );
    }
}