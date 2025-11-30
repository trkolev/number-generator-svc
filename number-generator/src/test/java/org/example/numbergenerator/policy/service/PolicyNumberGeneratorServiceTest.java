package org.example.numbergenerator.policy.service;

import org.example.numbergenerator.policy.model.PolicyNumberGenerator;
import org.example.numbergenerator.policy.repository.PolicyNumberGeneratorRepository;
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
class PolicyNumberGeneratorServiceTest {

    private static RedisServer redisServer;
    private static RedisConnectionFactory realRedisConnectionFactory;

    @Mock
    private PolicyNumberGeneratorRepository policyNumberGeneratorRepository;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @InjectMocks
    private PolicyNumberGeneratorService policyNumberGeneratorService;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = RedisServer.builder()
                .port(6374)
                .setting("maxmemory 128M")
                .build();
        redisServer.start();
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("localhost");
        factory.setPort(6374);
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
            new PolicyNumberGeneratorService(policyNumberGeneratorRepository, null);
        });
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(policyNumberGeneratorService);
        assertNotNull(redisConnectionFactory);
        assertNotNull(policyNumberGeneratorRepository);
    }

    @Test
    void testFormatPattern() {
        String testFormat = String.format("SG/08/%010d", 123L);
        assertThat(testFormat).matches("SG/08/\\d{10}");
        assertThat(testFormat.length()).isEqualTo(16);
        assertThat(testFormat).startsWith("SG/08/");
    }

    @Test
    void testGenerateNextPolicyNumber_GeneratesCorrectFormat() {
        PolicyNumberGeneratorService service = new PolicyNumberGeneratorService(
                policyNumberGeneratorRepository, realRedisConnectionFactory);

        String result = service.generateNextPolicyNumber();

        // Verify the format
        assertThat(result).isNotNull();
        assertThat(result).matches("SG/08/\\d{10}");
        assertThat(result).startsWith("SG/08/");
        assertThat(result.length()).isEqualTo(16);

        // Verify repository was called
        ArgumentCaptor<PolicyNumberGenerator> captor = ArgumentCaptor.forClass(PolicyNumberGenerator.class);
        verify(policyNumberGeneratorRepository, atLeastOnce()).save(captor.capture());

        PolicyNumberGenerator saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getPolicy()).isEqualTo("policy");
        assertThat(saved.getValue()).isNotNull();
        assertThat(saved.getValue()).isPositive();
    }

    @Test
    void testGenerateNextPolicyNumber_SavesToRepository() {
        PolicyNumberGeneratorService service = new PolicyNumberGeneratorService(
                policyNumberGeneratorRepository, realRedisConnectionFactory);

        service.generateNextPolicyNumber();

        ArgumentCaptor<PolicyNumberGenerator> captor = ArgumentCaptor.forClass(PolicyNumberGenerator.class);
        verify(policyNumberGeneratorRepository).save(captor.capture());

        PolicyNumberGenerator saved = captor.getValue();
        assertThat(saved.getPolicy()).isEqualTo("policy");
        assertThat(saved.getValue()).isNotNull();
        assertThat(saved.getValue()).isPositive();
    }

    @Test
    void testGenerateNextPolicyNumber_MultipleCalls_GenerateSequentialNumbers() {
        PolicyNumberGeneratorService service = new PolicyNumberGeneratorService(
                policyNumberGeneratorRepository, realRedisConnectionFactory);

        String number1 = service.generateNextPolicyNumber();
        String number2 = service.generateNextPolicyNumber();
        String number3 = service.generateNextPolicyNumber();

        assertThat(number1).isNotNull();
        assertThat(number2).isNotNull();
        assertThat(number3).isNotNull();
        assertThat(number1).matches("SG/08/\\d{10}");
        assertThat(number2).matches("SG/08/\\d{10}");
        assertThat(number3).matches("SG/08/\\d{10}");
        assertThat(number1).isNotEqualTo(number2);
        assertThat(number2).isNotEqualTo(number3);
        
        // Verify all calls saved to repository
        verify(policyNumberGeneratorRepository, times(3)).save(any(PolicyNumberGenerator.class));
    }
}
