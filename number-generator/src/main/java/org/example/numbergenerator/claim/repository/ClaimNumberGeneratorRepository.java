package org.example.numbergenerator.claim.repository;

import org.example.numbergenerator.claim.model.ClaimNumberGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimNumberGeneratorRepository extends JpaRepository<ClaimNumberGenerator, String> {
}
