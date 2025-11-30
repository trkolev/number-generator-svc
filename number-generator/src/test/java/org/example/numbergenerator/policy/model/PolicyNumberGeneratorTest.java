package org.example.numbergenerator.policy.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolicyNumberGeneratorTest {

    @Test
    void testNoArgsConstructor() {
        PolicyNumberGenerator generator = new PolicyNumberGenerator();

        assertNotNull(generator);
        assertNull(generator.getPolicy());
        assertNull(generator.getValue());
    }

    @Test
    void testAllArgsConstructor() {
        String policy = "policy";
        Long value = 12345L;

        PolicyNumberGenerator generator = new PolicyNumberGenerator();
        generator.setPolicy(policy);
        generator.setValue(value);

        assertNotNull(generator);
        assertEquals(policy, generator.getPolicy());
        assertEquals(value, generator.getValue());
    }

    @Test
    void testSettersAndGetters() {
        PolicyNumberGenerator generator = new PolicyNumberGenerator();
        String policy = "test-policy";
        Long value = 99999L;

        generator.setPolicy(policy);
        generator.setValue(value);

        assertEquals(policy, generator.getPolicy());
        assertEquals(value, generator.getValue());
    }

    @Test
    void testEqualsAndHashCode() {
        PolicyNumberGenerator generator1 = new PolicyNumberGenerator();
        generator1.setPolicy("policy");
        generator1.setValue(1L);
        PolicyNumberGenerator generator2 = new PolicyNumberGenerator();
        generator2.setPolicy("policy");
        generator2.setValue(1L);
        PolicyNumberGenerator generator3 = new PolicyNumberGenerator();
        generator3.setPolicy("policy");
        generator3.setValue(2L);

        assertNotNull(generator1);
        assertNotNull(generator2);
        assertNotNull(generator3);
        assertEquals("policy", generator1.getPolicy());
        assertEquals(Long.valueOf(1L), generator1.getValue());
    }

    @Test
    void testToString() {
        PolicyNumberGenerator generator = new PolicyNumberGenerator();
        generator.setPolicy("policy");
        generator.setValue(123L);

        String toString = generator.toString();

        assertNotNull(toString);
    }
}
