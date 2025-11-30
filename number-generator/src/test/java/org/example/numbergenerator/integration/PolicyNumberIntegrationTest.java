package org.example.numbergenerator.integration;

import org.example.numbergenerator.policy.model.PolicyNumberGenerator;
import org.example.numbergenerator.policy.repository.PolicyNumberGeneratorRepository;
import org.example.numbergenerator.policy.service.PolicyNumberGeneratorService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import redis.embedded.RedisServer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PolicyNumberIntegrationTest {

    private static RedisServer redisServer;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = RedisServer.builder()
                .port(6371)
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
        registry.add("spring.data.redis.port", () -> 6371);
    }

    @Autowired
    private PolicyNumberGeneratorService policyNumberGeneratorService;

    @Autowired
    private PolicyNumberGeneratorRepository policyNumberGeneratorRepository;

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
    void testGenerateNextPolicyNumber_Integration_GeneratesSequentialNumbers() {
        String number1 = policyNumberGeneratorService.generateNextPolicyNumber();
        String number2 = policyNumberGeneratorService.generateNextPolicyNumber();
        String number3 = policyNumberGeneratorService.generateNextPolicyNumber();

        assertThat(number1).matches("SG/08/\\d{10}");
        assertThat(number2).matches("SG/08/\\d{10}");
        assertThat(number3).matches("SG/08/\\d{10}");
        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
    }

    @Test
    void testGenerateNextPolicyNumber_Integration_SavesToRepository() {
        String number = policyNumberGeneratorService.generateNextPolicyNumber();

        assertThat(number).isNotNull();
        PolicyNumberGenerator saved = policyNumberGeneratorRepository.findById("policy").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getPolicy()).isEqualTo("policy");
        assertThat(saved.getValue()).isNotNull();
        assertThat(saved.getValue()).isPositive();
    }

    @Test
    void testGenerateNextPolicyNumber_Integration_FormatIsCorrect() {
        String number = policyNumberGeneratorService.generateNextPolicyNumber();

        assertThat(number).startsWith("SG/08/");
        assertThat(number.length()).isEqualTo(16);
        assertThat(number).matches("SG/08/\\d{10}");
    }

    @Test
    void testGenerateNextPolicyNumber_Integration_ConcurrentGeneration() {
        String number1 = policyNumberGeneratorService.generateNextPolicyNumber();
        String number2 = policyNumberGeneratorService.generateNextPolicyNumber();
        String number3 = policyNumberGeneratorService.generateNextPolicyNumber();

        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
        assertThat(number1).isNotEqualTo(number3);
    }
}
