package org.example.numbergenerator.integration;

import org.example.numbergenerator.claim.service.ClaimNumberGeneratorService;
import org.example.numbergenerator.policy.service.PolicyNumberGeneratorService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import redis.embedded.RedisServer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FullIntegrationTest {

    private static RedisServer redisServer;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = RedisServer.builder()
                .port(6372)
                .setting("maxmemory 128M")
                .build();
        redisServer.start();
    }

    @AfterAll
    static void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> 6372);
    }

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private ClaimNumberGeneratorService claimNumberGeneratorService;

    @Autowired
    private PolicyNumberGeneratorService policyNumberGeneratorService;

    @Autowired
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void setUp() {
        if (redisConnectionFactory != null) {
            try (RedisConnection connection = redisConnectionFactory.getConnection()) {
                if (connection != null) {
                    connection.serverCommands().flushAll();
                }
            }
        }
    }

    @Test
    void testClaimNumberEndpoint_FullIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/claim-numbers",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).matches("Cl-\\d{10}");
    }

    @Test
    void testPolicyNumberEndpoint_FullIntegration() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/policy-numbers",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).matches("SG/08/\\d{10}");
    }

    @Test
    void testBothEndpoints_FullIntegration_WorkIndependently() {
        ResponseEntity<String> claimResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/claim-numbers",
                String.class);

        ResponseEntity<String> policyResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/policy-numbers",
                String.class);

        assertThat(claimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(policyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(claimResponse.getBody()).matches("Cl-\\d{10}");
        assertThat(policyResponse.getBody()).matches("SG/08/\\d{10}");
        assertThat(claimResponse.getBody()).isNotEqualTo(policyResponse.getBody());
    }

    @Test
    void testServiceMethods_Integration_WorkCorrectly() {
        // Test that services are properly wired
        assertThat(claimNumberGeneratorService).isNotNull();
        assertThat(policyNumberGeneratorService).isNotNull();
    }
}
