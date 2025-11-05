package org.example.numbergenerator.claim.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimNumberGenerator {

    @Id
    private String claim;

    @Column(nullable = false)
    private Long value;

}
