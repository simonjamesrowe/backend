package com.simonjamesrowe.backend.test;

import com.simonjamesrowe.component.test.kafka.WithKafkaContainer;
import org.junit.jupiter.api.Test;

@WithKafkaContainer
class BackendApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }
}