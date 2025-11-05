package org.example.numbergenerator.web;

import org.example.numbergenerator.policy.service.PolicyNumberGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/policy-numbers")
public class PolicyNumberController {

    private final PolicyNumberGeneratorService policyNumberGeneratorService;


    public PolicyNumberController(PolicyNumberGeneratorService policyNumberGeneratorService) {
        this.policyNumberGeneratorService = policyNumberGeneratorService;
    }

    @GetMapping()
    public ResponseEntity<String> returnNextPolicyNumber() {

        return ResponseEntity.ok(policyNumberGeneratorService.generateNextPolicyNumber());

    }

}
