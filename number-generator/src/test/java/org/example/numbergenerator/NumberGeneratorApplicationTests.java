package org.example.numbergenerator;

import org.example.numbergenerator.claim.repository.ClaimNumberGeneratorRepository;
import org.example.numbergenerator.policy.repository.PolicyNumberGeneratorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
class NumberGeneratorApplicationTests {

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockBean
    private ClaimNumberGeneratorRepository claimNumberGeneratorRepository;

    @MockBean
    private PolicyNumberGeneratorRepository policyNumberGeneratorRepository;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        // Redis and repositories are mocked, so no actual Redis connection is needed
    }
}
