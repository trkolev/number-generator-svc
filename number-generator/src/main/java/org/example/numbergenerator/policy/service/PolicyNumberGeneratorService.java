package org.example.numbergenerator.policy.service;

import jakarta.transaction.Transactional;
import org.example.numbergenerator.policy.model.PolicyNumberGenerator;
import org.example.numbergenerator.policy.repository.PolicyNumberGeneratorRepository;
import org.springframework.stereotype.Service;

@Service
public class PolicyNumberGeneratorService {

    private final PolicyNumberGeneratorRepository policyNumberGeneratorRepository;

    public PolicyNumberGeneratorService(PolicyNumberGeneratorRepository counterRepository) {
        this.policyNumberGeneratorRepository = counterRepository;
    }

    @Transactional
    public synchronized String generateNextPolicyNumber() {
        PolicyNumberGenerator counter = policyNumberGeneratorRepository.findById("policy")
                .orElseGet(() -> new PolicyNumberGenerator("policy", 0L));

        long next = counter.getValue() + 1;
        counter.setValue(next);
        policyNumberGeneratorRepository.save(counter);

        return String.format("SG/08/%010d", next);
    }

}
