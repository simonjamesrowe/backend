package com.simonjamesrowe.apigateway.test.restcontroller;

import com.simonjamesrowe.component.test.BaseComponentTest;
import com.simonjamesrowe.component.test.ComponentTest;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ComponentTest
class CmsProxyControllerTest extends BaseComponentTest {

    @Test
    void shouldProxyJobsRequest() {
        stubFor(get(urlEqualTo("/jobs"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"id\":\"1\",\"company\":\"Test Corp\"}]")));

        given()
            .log().all()
            .accept("application/json")
            .when()
            .get("/jobs")
            .then()
            .log().all()
            .statusCode(200)
            .contentType("application/json")
            .body("[0].id", equalTo("1"))
            .body("[0].company", equalTo("Test Corp"));
    }

    @Test
    void shouldProxySkillsRequest() {
        stubFor(get(urlEqualTo("/skills"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"name\":\"Java\",\"rating\":9}]")));

        given()
            .log().all()
            .accept("application/json")
            .when()
            .get("/skills")
            .then()
            .log().all()
            .statusCode(200)
            .contentType("application/json")
            .body("[0].name", equalTo("Java"))
            .body("[0].rating", equalTo(9));
    }

    @Test
    void shouldProxyBlogsRequestWithQueryString() {
        stubFor(get(urlEqualTo("/blogs?published=true"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"id\":\"1\",\"title\":\"Test Blog\"}]")));

        given()
            .log().all()
            .accept("application/json")
            .queryParam("published", "true")
            .when()
            .get("/blogs")
            .then()
            .log().all()
            .statusCode(200)
            .contentType("application/json")
            .body("[0].id", equalTo("1"))
            .body("[0].title", equalTo("Test Blog"));
    }

    @Test
    void shouldProxyBlogByIdRequest() {
        stubFor(get(urlEqualTo("/blogs/123"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"123\",\"title\":\"Specific Blog\"}")));

        given()
            .log().all()
            .accept("application/json")
            .when()
            .get("/blogs/123")
            .then()
            .log().all()
            .statusCode(200)
            .contentType("application/json")
            .body("id", equalTo("123"))
            .body("title", equalTo("Specific Blog"));
    }

    @Test
    void shouldProxyAllCmsEndpoints() {
        String[] endpoints = {"/profiles", "/tags", "/skills-groups", "/social-medias", "/tour-steps"};

        for (String endpoint : endpoints) {
            stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]")));

            given()
                .accept("application/json")
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body(equalTo("[]"));
        }
    }
}
