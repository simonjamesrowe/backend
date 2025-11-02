package com.simonjamesrowe.component.test.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.simonjamesrowe.component.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(stubs = "classpath:META-INF/mappings/*.json", port = 0)
class JwtUtilsTest extends BaseIntegrationTest {

    @Value("http://localhost:${wiremock.server.port}/auth/realms/master/protocol/openid-connect/certs")
    private String jwksUrl;

    @Test
    public void testCanGenerateJwtWithRSA256Signer() throws Exception {
        assertThat(JwtUtils.signedJWTTokenRSA256(UUID.randomUUID().toString(), "Simon Rowe",
                "simon@y-tree.com", "simon@y-tree.com")).isNotBlank();
    }

    @Test
    public void testCanGenerateJwtWithRSA256SignerH256Signer() throws Exception {
        assertThat(JwtUtils.signedJWTTokenHS256(UUID.randomUUID().toString(), "Simon Rowe",
                "simon@y-tree.com", "simon@y-tree.com")).isNotNull();
    }

    @Test
    public void testWireMockCertsEndpointExists() {
        String jwkResponse = new RestTemplate().getForObject(jwksUrl, String.class);
        // Verify that the WireMock endpoint returns the expected JWK response structure
        assertThat(jwkResponse).contains("\"kty\":\"RSA\"");
        assertThat(jwkResponse).contains("\"kid\":\"sampleKey\"");
        assertThat(jwkResponse).contains("\"alg\":\"RS256\"");
        assertThat(jwkResponse).contains("\"keys\":");
    }

    @Test
    public void testClaims() throws Exception {
        JWTClaimsSet claimsSet = JwtUtils.claims(UUID.randomUUID().toString(), "Simon Rowe",
                "simon@y-tree.com", "simon@y-tree.com", null);
        assertThat(claimsSet.getJSONObjectClaim("realm_access")).isNull();

        claimsSet = JwtUtils.claims(UUID.randomUUID().toString(), "Simon Rowe",
                "simon@y-tree.com", "simon@y-tree.com", Arrays.asList("actuator"));
        assertThat(claimsSet.getJSONObjectClaim("realm_access")).isNotNull();
        assertThat(claimsSet.getJSONObjectClaim("realm_access"))
                .hasFieldOrPropertyWithValue("roles", Arrays.asList("actuator"));
    }
}