package org.example.numbergenerator.policy.repository;

import org.example.numbergenerator.policy.model.PolicyNumberGenerator;
import org.springframework.data.repository.CrudRepository;

public interface PolicyNumberGeneratorRepository extends CrudRepository<PolicyNumberGenerator, String> {
}
