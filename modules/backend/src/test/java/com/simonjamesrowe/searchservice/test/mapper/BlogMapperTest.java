package com.simonjamesrowe.searchservice.test.mapper;

import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.TagResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.searchservice.core.model.IndexBlogRequest;
import com.simonjamesrowe.searchservice.core.model.IndexSiteRequest;
import com.simonjamesrowe.searchservice.mapper.BlogMapper;
import com.simonjamesrowe.searchservice.test.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

class BlogMapperTest {

    @Test
    void shouldConvertToBlogIndexRequest() {
        TagResponseDTO tag1 = new TagResponseDTO("tag1", "Tag 1",
                ZonedDateTime.now(), ZonedDateTime.now(), 1);
        TagResponseDTO tag2 = new TagResponseDTO("tag2", "Tag 2",
                ZonedDateTime.now(), ZonedDateTime.now(), 2);
        SkillResponseDTO skill1 = new SkillResponseDTO("skill1", "Skill 1",
                ZonedDateTime.now(), ZonedDateTime.now(), "Skill desc", 5.0, 1, null, true);
        SkillResponseDTO skill2 = new SkillResponseDTO("skill2", "Skill 2",
                ZonedDateTime.now(), ZonedDateTime.now(), "Skill desc 2", 4.0, 2, null, false);

        BlogResponseDTO input = new BlogResponseDTO(
            "blog1",
            true,
            List.of(tag1, tag2),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            List.of(skill1, skill2),
            "Short description",
            "Test Blog",
            "Blog content",
            TestUtils.image("test", 400)
        );

        IndexBlogRequest expected = new IndexBlogRequest(
            input.id(),
            input.title(),
            input.shortDescription(),
            input.content(),
            List.of("Tag 1", "Tag 2"),
            List.of("Skill 1", "Skill 2"),
            "uploads/test-thumb.jpg",
            "uploads/test-sml.jpg",
            "uploads/test-med.jpg",
            input.createdAt().toLocalDate(),
            input.published()
        );

        Assertions.assertThat(BlogMapper.toBlogIndexRequest(input)).isEqualTo(expected);
    }

    @Test
    void shouldConvertToIndexSiteRequest() {
        BlogResponseDTO input = new BlogResponseDTO(
            "blog1",
            true,
            List.of(),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            List.of(),
            "Short description",
            "Test Blog",
            "Blog content",
            TestUtils.image("test", 400)
        );

        IndexSiteRequest expected = new IndexSiteRequest(
            "blog_blog1",
            "/blogs/blog1",
            "Test Blog",
            "Blogs",
            "uploads/test-thumb.jpg",
            "Short description",
            "Blog content"
        );

        Assertions.assertThat(BlogMapper.toSiteIndexRequest(input)).isEqualTo(expected);
    }
}