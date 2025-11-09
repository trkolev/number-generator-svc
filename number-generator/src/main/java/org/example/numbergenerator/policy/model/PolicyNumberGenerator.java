package org.example.numbergenerator.policy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RedisHash("policyNumberGenerator")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PolicyNumberGenerator {

    @Id
    private String policy;

    private Long value;

}