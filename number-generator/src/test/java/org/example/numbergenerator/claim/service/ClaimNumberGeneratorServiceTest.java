package org.example.numbergenerator.claim.service;

import org.example.numbergenerator.claim.model.ClaimNumberGenerator;
import org.example.numbergenerator.claim.repository.ClaimNumberGeneratorRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimNumberGeneratorServiceTest {

    private static RedisServer redisServer;
    private static RedisConnectionFactory realRedisConnectionFactory;

    @Mock
    private ClaimNumberGeneratorRepository claimNumberGeneratorRepository;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @InjectMocks
    private ClaimNumberGeneratorService claimNumberGeneratorService;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = RedisServer.builder()
                .port(6373)
                .setting("maxmemory 128M")
                .build();
        redisServer.start();
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("localhost");
        factory.setPort(6373);
        factory.afterPropertiesSet();
        realRedisConnectionFactory = factory;
    }

    @AfterAll
    static void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Test
    void testConstructor_WithNullRedisConnectionFactory_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new ClaimNumberGeneratorService(claimNumberGeneratorRepository, null);
        });
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(claimNumberGeneratorService);
        assertNotNull(redisConnectionFactory);
        assertNotNull(claimNumberGeneratorRepository);
    }

    @Test
    void testFormatPattern() {
        String testFormat = String.format("Cl-%010d", 123L);
        assertThat(testFormat).matches("Cl-\\d{10}");
        assertThat(testFormat.length()).isEqualTo(13);
        assertThat(testFormat).startsWith("Cl-");
    }

    @Test
    void testGenerateNextClaimNumber_GeneratesCorrectFormat() {
        ClaimNumberGeneratorService service = new ClaimNumberGeneratorService(
                claimNumberGeneratorRepository, realRedisConnectionFactory);

        String result = service.generateNextClaimNumber();

        // Verify the format
        assertThat(result).isNotNull();
        assertThat(result).matches("Cl-\\d{10}");
        assertThat(result).startsWith("Cl-");
        assertThat(result.length()).isEqualTo(13);

        // Verify repository was called
        ArgumentCaptor<ClaimNumberGenerator> captor = ArgumentCaptor.forClass(ClaimNumberGenerator.class);
        verify(claimNumberGeneratorRepository, atLeastOnce()).save(captor.capture());

        ClaimNumberGenerator saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getClaim()).isEqualTo("claim");
        assertThat(saved.getValue()).isNotNull();
        assertThat(saved.getValue()).isPositive();
    }

    @Test
    void testGenerateNextClaimNumber_SavesToRepository() {
        ClaimNumberGeneratorService service = new ClaimNumberGeneratorService(
                claimNumberGeneratorRepository, realRedisConnectionFactory);

        service.generateNextClaimNumber();

        ArgumentCaptor<ClaimNumberGenerator> captor = ArgumentCaptor.forClass(ClaimNumberGenerator.class);
        verify(claimNumberGeneratorRepository).save(captor.capture());

        ClaimNumberGenerator saved = captor.getValue();
        assertThat(saved.getClaim()).isEqualTo("claim");
        assertThat(saved.getValue()).isNotNull();
        assertThat(saved.getValue()).isPositive();
    }

    @Test
    void testGenerateNextClaimNumber_MultipleCalls_GenerateSequentialNumbers() {
        ClaimNumberGeneratorService service = new ClaimNumberGeneratorService(
                claimNumberGeneratorRepository, realRedisConnectionFactory);

        String number1 = service.generateNextClaimNumber();
        String number2 = service.generateNextClaimNumber();
        String number3 = service.generateNextClaimNumber();

        assertThat(number1).isNotNull();
        assertThat(number2).isNotNull();
        assertThat(number3).isNotNull();
        assertThat(number1).matches("Cl-\\d{10}");
        assertThat(number2).matches("Cl-\\d{10}");
        assertThat(number3).matches("Cl-\\d{10}");
        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
        
        // Verify all calls saved to repository
        verify(claimNumberGeneratorRepository, times(3)).save(any(ClaimNumberGenerator.class));
    }
}
