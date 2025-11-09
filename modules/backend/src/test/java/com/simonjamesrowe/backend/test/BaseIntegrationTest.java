package com.simonjamesrowe.backend.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.simonjamesrowe.component.test.elasticsearch.WithElasticsearchContainer;
import com.simonjamesrowe.component.test.kafka.WithKafkaContainer;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.env.Environment;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithKafkaContainer
@WithElasticsearchContainer
@AutoConfigureWireMock(stubs = "classpath:META-INF/mappings/*.json", port = 0)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WireMockServer wireMockServer;

    @Autowired
    protected Environment environment;

    @BeforeEach
    public void setupRestAssured() {
        wireMockServer.resetRequests();
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;
    }
}
