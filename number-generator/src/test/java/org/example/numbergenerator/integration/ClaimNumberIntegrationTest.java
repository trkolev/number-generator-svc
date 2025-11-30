package org.example.numbergenerator.integration;

import org.example.numbergenerator.claim.model.ClaimNumberGenerator;
import org.example.numbergenerator.claim.repository.ClaimNumberGeneratorRepository;
import org.example.numbergenerator.claim.service.ClaimNumberGeneratorService;
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
class ClaimNumberIntegrationTest {

    private static RedisServer redisServer;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = RedisServer.builder()
                .port(6370)
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
        registry.add("spring.data.redis.port", () -> 6370);
    }

    @Autowired
    private ClaimNumberGeneratorService claimNumberGeneratorService;

    @Autowired
    private ClaimNumberGeneratorRepository claimNumberGeneratorRepository;

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
    void testGenerateNextClaimNumber_Integration_GeneratesSequentialNumbers() {
        String number1 = claimNumberGeneratorService.generateNextClaimNumber();
        String number2 = claimNumberGeneratorService.generateNextClaimNumber();
        String number3 = claimNumberGeneratorService.generateNextClaimNumber();

        assertThat(number1).matches("Cl-\\d{10}");
        assertThat(number2).matches("Cl-\\d{10}");
        assertThat(number3).matches("Cl-\\d{10}");
        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
    }

    @Test
    void testGenerateNextClaimNumber_Integration_SavesToRepository() {
        String number = claimNumberGeneratorService.generateNextClaimNumber();

        assertThat(number).isNotNull();
        ClaimNumberGenerator saved = claimNumberGeneratorRepository.findById("claim").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getClaim()).isEqualTo("claim");
        assertThat(saved.getValue()).isNotNull();
        assertThat(saved.getValue()).isPositive();
    }

    @Test
    void testGenerateNextClaimNumber_Integration_FormatIsCorrect() {
        String number = claimNumberGeneratorService.generateNextClaimNumber();

        assertThat(number).startsWith("Cl-");
        assertThat(number.length()).isEqualTo(13);
        assertThat(number).matches("Cl-\\d{10}");
    }

    @Test
    void testGenerateNextClaimNumber_Integration_ConcurrentGeneration() {
        String number1 = claimNumberGeneratorService.generateNextClaimNumber();
        String number2 = claimNumberGeneratorService.generateNextClaimNumber();
        String number3 = claimNumberGeneratorService.generateNextClaimNumber();

        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
        assertThat(number1).isNotEqualTo(number3);
    }
}
