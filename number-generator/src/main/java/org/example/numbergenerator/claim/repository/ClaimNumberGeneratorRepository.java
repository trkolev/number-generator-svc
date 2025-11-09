package org.example.numbergenerator.claim.repository;

import org.example.numbergenerator.claim.model.ClaimNumberGenerator;
import org.springframework.data.repository.CrudRepository;

public interface ClaimNumberGeneratorRepository extends CrudRepository<ClaimNumberGenerator, String> {
}
