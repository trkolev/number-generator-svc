package org.example.numbergenerator.policy.repository;

import org.example.numbergenerator.policy.model.PolicyNumberGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyNumberGeneratorRepository extends JpaRepository<PolicyNumberGenerator, String> {
}
