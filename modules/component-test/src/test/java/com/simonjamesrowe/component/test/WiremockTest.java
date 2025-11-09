package com.simonjamesrowe.component.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(stubs = "classpath:META-INF/mappings/*.json", port = 0)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WiremockTest extends BaseIntegrationTest {

    @Value("http://localhost:${wiremock.server.port}/test")
    private String testEndpoint;

    @Value("${wiremock.server.port}")
    private int wiremockPort;

    @Test
    public void testCanAccessWiremockServer() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> helloWorld = restTemplate.getForObject(testEndpoint, Map.class);
        assertThat(helloWorld).containsEntry("hello", "world");
        assertThat(wiremockPort).isNotEqualTo(8080);
    }

}
