package org.example.numbergenerator.claim.service;

import java.util.Objects;

import org.example.numbergenerator.claim.model.ClaimNumberGenerator;
import org.example.numbergenerator.claim.repository.ClaimNumberGeneratorRepository;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ClaimNumberGeneratorService {

    private static final String CLAIM_COUNTER_KEY = "number-generator:claim:sequence";

    private final ClaimNumberGeneratorRepository claimNumberGeneratorRepository;
    private final @NonNull RedisConnectionFactory redisConnectionFactory;

    public ClaimNumberGeneratorService(ClaimNumberGeneratorRepository claimNumberGeneratorRepository,
                                       @NonNull RedisConnectionFactory redisConnectionFactory) {
        this.claimNumberGeneratorRepository = claimNumberGeneratorRepository;
        this.redisConnectionFactory = Objects.requireNonNull(redisConnectionFactory, "redisConnectionFactory must not be null");
    }

    public String generateNextClaimNumber() {
        RedisAtomicLong counter = new RedisAtomicLong(CLAIM_COUNTER_KEY, redisConnectionFactory);
        long next = counter.incrementAndGet();
        claimNumberGeneratorRepository.save(new ClaimNumberGenerator("claim", next));

        return String.format("Cl-%010d", next);
    }

}
