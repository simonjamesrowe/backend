package com.simonjamesrowe.searchservice.entrypoints.kafka;

import com.simonjamesrowe.model.cms.dto.WebhookEventDTO;

import java.util.List;

public interface IKafkaEventConsumer {
    void consumeEvents(List<WebhookEventDTO> events);
}