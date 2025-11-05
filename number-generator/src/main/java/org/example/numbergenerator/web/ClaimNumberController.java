package org.example.numbergenerator.web;

import org.example.numbergenerator.claim.service.ClaimNumberGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/claim-numbers")
public class ClaimNumberController {

    private final ClaimNumberGeneratorService claimNumberGeneratorService;

    public ClaimNumberController(ClaimNumberGeneratorService claimNumberGeneratorService) {
        this.claimNumberGeneratorService = claimNumberGeneratorService;
    }

    @GetMapping()
    public ResponseEntity<String> returnNextClaimNumber() {

        return ResponseEntity.ok(claimNumberGeneratorService.generateNextClaimNumber());

    }
}
