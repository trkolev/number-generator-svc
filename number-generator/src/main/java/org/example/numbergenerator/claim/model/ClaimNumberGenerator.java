package org.example.numbergenerator.claim.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@RedisHash("claimNumberGenerator")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimNumberGenerator {

    @Id
    private String claim;

    private Long value;

}
