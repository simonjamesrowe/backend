package com.simonjamesrowe.apigateway.test;

import com.simonjamesrowe.component.test.BaseComponentTest;
import com.simonjamesrowe.component.test.ComponentTest;
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer;
import org.junit.jupiter.api.Test;

@ComponentTest
@WithKafkaContainer
class ApiGatewayApplicationTests extends BaseComponentTest {

    @Test
    void contextLoads() {
    }
}