package com.simonjamesrowe.apigateway.entrypoints.restcontroller;

import brave.Span;
import brave.Tracer;
import com.simonjamesrowe.apigateway.core.usecase.IResumeUseCase;
import com.simonjamesrowe.model.cms.dto.WebhookEventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class WebhookController {

    private final KafkaTemplate<String, WebhookEventDTO> kafkaTemplate;
    private final IResumeUseCase resumeUseCase;
    private final String eventTopic;
    private final Tracer tracer;

    public WebhookController(
            KafkaTemplate<String, WebhookEventDTO> kafkaTemplate,
            IResumeUseCase resumeUseCase,
            @Value("${namespace:LOCAL}_EVENTS") String eventTopic,
            Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.resumeUseCase = resumeUseCase;
        this.eventTopic = eventTopic;
        this.tracer = tracer;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhookPost(@RequestBody WebhookEventDTO event) {
        resumeUseCase.regenerateResume();
        sendToKafka(event);
        return ResponseEntity.ok().build();
    }

    private void sendToKafka(WebhookEventDTO event) {
        Span span = tracer.nextSpan().name("sendToKafka").start();
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            String messageKey = event.model() + "-" + event.entry().get("id").textValue();

            kafkaTemplate.send(
                    MessageBuilder
                            .withPayload(event)
                            .setHeader("model", event.model())
                            .setHeader(KafkaHeaders.TOPIC, eventTopic)
                            .setHeader("b3", span.context().traceId())
                            .setHeader(KafkaHeaders.KEY, messageKey)
                            .build()
            );
        } finally {
            span.finish();
        }
    }
}