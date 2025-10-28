package com.simonjamesrowe.searchservice.test.entrypoints.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonjamesrowe.component.test.BaseComponentTest;
import com.simonjamesrowe.component.test.ComponentTest;
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer;
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer;
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO;
import com.simonjamesrowe.model.cms.dto.SkillResponseDTO;
import com.simonjamesrowe.model.cms.dto.TagResponseDTO;
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocument;
import com.simonjamesrowe.searchservice.dataproviders.elasticsearch.blog.BlogDocumentRepository;
import com.simonjamesrowe.searchservice.test.TestUtils;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@WithKafkaContainer
@WithElasticsearchContainer
@ComponentTest
@ActiveProfiles("cms")
class KafkaEventConsumerITest extends BaseComponentTest {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlogDocumentRepository blogDocumentRepository;

    @BeforeEach
    @AfterEach
    void clearElasticSearchDocuments() {
        blogDocumentRepository.deleteAll();
    }

    @Test
    void blogEventsFromKafkaShouldBeIndexed() {
        BlogResponseDTO blog = new BlogResponseDTO(
            "1",
            true,
            List.of(
                new TagResponseDTO("1", "Kubernetes", ZonedDateTime.now(), ZonedDateTime.now(), 1),
                new TagResponseDTO("2", "Jenkins", ZonedDateTime.now(), ZonedDateTime.now(), 1)
            ),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            List.of(
                new SkillResponseDTO("1", "Jenkins", ZonedDateTime.now(), ZonedDateTime.now(),
                    "Jenkins Description", 7.8, 2, TestUtils.image("jenkins", 10), true)
            ),
            "Short description",
            "My first blog",
            "Some awesome content",
            TestUtils.image("image1", 10)
        );

        BlogResponseDTO blog2 = new BlogResponseDTO(
            "2",
            true,
            List.of(
                new TagResponseDTO("3", "Spring", ZonedDateTime.now(), ZonedDateTime.now(), 1),
                new TagResponseDTO("4", "Kotlin", ZonedDateTime.now(), ZonedDateTime.now(), 1)
            ),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            List.of(
                new SkillResponseDTO("5", "TestContainers", ZonedDateTime.now(), ZonedDateTime.now(),
                    "TestContainers Description", 4.0, 1, TestUtils.image("test-containers", 5), true)
            ),
            "Short description 2",
            "My second blog",
            "Some awesome content again",
            TestUtils.image("image2", 6)
        );

        BlogResponseDTO blog3 = new BlogResponseDTO(
            "3",
            false, // not published
            List.of(
                new TagResponseDTO("3", "Spring", ZonedDateTime.now(), ZonedDateTime.now(), 1),
                new TagResponseDTO("4", "Kotlin", ZonedDateTime.now(), ZonedDateTime.now(), 1)
            ),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            List.of(
                new SkillResponseDTO("5", "TestContainers", ZonedDateTime.now(), ZonedDateTime.now(),
                    "TestContainers Description", 4.0, 1, TestUtils.image("testcontainers", 8), false)
            ),
            "Short description 3",
            "My 3rd blog",
            "Some awesome content again",
            TestUtils.image("image2", 9)
        );

        JsonNode blogNode = objectMapper.convertValue(blog, JsonNode.class);
        JsonNode blog2Node = objectMapper.convertValue(blog2, JsonNode.class);
        JsonNode blog3Node = objectMapper.convertValue(blog3, JsonNode.class);
        JsonNode carNode = objectMapper.convertValue(List.of("vals"), JsonNode.class);

        WebhookEventDTO event1 = new WebhookEventDTO("created", ZonedDateTime.now(), "blog", blogNode);
        WebhookEventDTO event2 = new WebhookEventDTO("created", ZonedDateTime.now(), "car", carNode);
        WebhookEventDTO event3 = new WebhookEventDTO("updated", ZonedDateTime.now(), "blog", blog2Node);
        WebhookEventDTO event4 = new WebhookEventDTO("updated", ZonedDateTime.now(), "blog", blog3Node);

        // Send events to Kafka
        kafkaTemplate.send(
            MessageBuilder.withPayload(event1)
                .setHeader(KafkaHeaders.TOPIC, "LOCAL_EVENTS")
                .build()
        );
        kafkaTemplate.send(
            MessageBuilder.withPayload(event2)
                .setHeader(KafkaHeaders.TOPIC, "LOCAL_EVENTS")
                .build()
        );
        kafkaTemplate.send(
            MessageBuilder.withPayload(event3)
                .setHeader(KafkaHeaders.TOPIC, "LOCAL_EVENTS")
                .build()
        );
        kafkaTemplate.send(
            MessageBuilder.withPayload(event4)
                .setHeader(KafkaHeaders.TOPIC, "LOCAL_EVENTS")
                .build()
        );

        // Wait for processing and verify results
        Awaitility.await().atMost(Duration.ofSeconds(60)).until(() ->
            blogDocumentRepository.count() == 2L
        );

        Optional<BlogDocument> doc2Optional = blogDocumentRepository.findById("2");
        Assertions.assertThat(doc2Optional).isPresent();

        BlogDocument doc2 = doc2Optional.get();
        Assertions.assertThat(doc2.getId()).isEqualTo("2");
        Assertions.assertThat(doc2.getTitle()).isEqualTo("My second blog");
        Assertions.assertThat(doc2.getContent()).isEqualTo("Some awesome content again");
        Assertions.assertThat(doc2.getTags()).isEqualTo(List.of("Spring", "Kotlin"));
        Assertions.assertThat(doc2.getSkills()).isEqualTo(List.of("TestContainers"));
        Assertions.assertThat(doc2.getThumbnailImage()).isEqualTo("uploads/image2-thumb.jpg");
        Assertions.assertThat(doc2.getSmallImage()).isEqualTo("uploads/image2-sml.jpg");
        Assertions.assertThat(doc2.getMediumImage()).isEqualTo("uploads/image2-med.jpg");
        Assertions.assertThat(doc2.getCreatedDate()).isEqualTo(LocalDate.now());
    }
}