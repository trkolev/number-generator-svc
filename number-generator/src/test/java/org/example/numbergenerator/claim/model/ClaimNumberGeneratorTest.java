package org.example.numbergenerator.claim.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClaimNumberGeneratorTest {

    @Test
    void testNoArgsConstructor() {
        ClaimNumberGenerator generator = new ClaimNumberGenerator();

        assertNotNull(generator);
        assertNull(generator.getClaim());
        assertNull(generator.getValue());
    }

    @Test
    void testAllArgsConstructor() {
        String claim = "claim";
        Long value = 12345L;

        ClaimNumberGenerator generator = new ClaimNumberGenerator();
        generator.setClaim(claim);
        generator.setValue(value);

        assertNotNull(generator);
        assertEquals(claim, generator.getClaim());
        assertEquals(value, generator.getValue());
    }

    @Test
    void testSettersAndGetters() {
        ClaimNumberGenerator generator = new ClaimNumberGenerator();
        String claim = "test-claim";
        Long value = 99999L;

        generator.setClaim(claim);
        generator.setValue(value);

        assertEquals(claim, generator.getClaim());
        assertEquals(value, generator.getValue());
    }

    @Test
    void testEqualsAndHashCode() {
        ClaimNumberGenerator generator1 = new ClaimNumberGenerator();
        generator1.setClaim("claim");
        generator1.setValue(1L);
        ClaimNumberGenerator generator2 = new ClaimNumberGenerator();
        generator2.setClaim("claim");
        generator2.setValue(1L);
        ClaimNumberGenerator generator3 = new ClaimNumberGenerator();
        generator3.setClaim("claim");
        generator3.setValue(2L);

        assertNotNull(generator1);
        assertNotNull(generator2);
        assertNotNull(generator3);
        assertEquals("claim", generator1.getClaim());
        assertEquals(Long.valueOf(1L), generator1.getValue());
    }

    @Test
    void testToString() {
        ClaimNumberGenerator generator = new ClaimNumberGenerator();
        generator.setClaim("claim");
        generator.setValue(123L);

        String toString = generator.toString();

        assertNotNull(toString);
    }
}
