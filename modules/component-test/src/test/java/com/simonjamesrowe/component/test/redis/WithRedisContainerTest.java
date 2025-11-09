package com.simonjamesrowe.component.test.redis;

import com.simonjamesrowe.component.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@WithRedisContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(stubs = "classpath:META-INF/mappings/*.json", port = 0)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithRedisContainerTest extends BaseIntegrationTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisOperationsWithTestContainer() {
        assertThat(redisTemplate).isNotNull();
        redisTemplate.opsForValue().set("key", "value");
        assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("value");
    }
}
