package org.example.numbergenerator.policy.service;

import java.util.Objects;

import org.example.numbergenerator.policy.model.PolicyNumberGenerator;
import org.example.numbergenerator.policy.repository.PolicyNumberGeneratorRepository;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PolicyNumberGeneratorService {

    private static final String POLICY_COUNTER_KEY = "number-generator:policy:sequence";

    private final PolicyNumberGeneratorRepository policyNumberGeneratorRepository;
    private final @NonNull RedisConnectionFactory redisConnectionFactory;

    public PolicyNumberGeneratorService(PolicyNumberGeneratorRepository counterRepository,
                                        @NonNull RedisConnectionFactory redisConnectionFactory) {
        this.policyNumberGeneratorRepository = counterRepository;
        this.redisConnectionFactory = Objects.requireNonNull(redisConnectionFactory, "redisConnectionFactory must not be null");
    }

    public String generateNextPolicyNumber() {
        RedisAtomicLong counter = new RedisAtomicLong(POLICY_COUNTER_KEY, redisConnectionFactory);
        long next = counter.incrementAndGet();
        PolicyNumberGenerator generator = new PolicyNumberGenerator();
        generator.setPolicy("policy");
        generator.setValue(Long.valueOf(next));
        policyNumberGeneratorRepository.save(generator);

        return String.format("SG/08/%010d", next);
    }

}
