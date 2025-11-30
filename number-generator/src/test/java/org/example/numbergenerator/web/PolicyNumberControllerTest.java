package org.example.numbergenerator.web;

import org.example.numbergenerator.policy.service.PolicyNumberGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyNumberController.class)
class PolicyNumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyNumberGeneratorService policyNumberGeneratorService;

    @Test
    void testReturnNextPolicyNumber_ReturnsOk() throws Exception {
        String expectedNumber = "SG/08/0000000001";
        when(policyNumberGeneratorService.generateNextPolicyNumber()).thenReturn(expectedNumber);

        mockMvc.perform(get("/api/v1/policy-numbers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(expectedNumber));
    }

    @Test
    void testReturnNextPolicyNumber_ReturnsCorrectFormat() throws Exception {
        String expectedNumber = "SG/08/0000000123";
        when(policyNumberGeneratorService.generateNextPolicyNumber()).thenReturn(expectedNumber);

        mockMvc.perform(get("/api/v1/policy-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("SG/08/0000000123"));
    }

    @Test
    void testReturnNextPolicyNumber_MultipleCalls_ReturnsDifferentNumbers() throws Exception {
        when(policyNumberGeneratorService.generateNextPolicyNumber())
                .thenReturn("SG/08/0000000001")
                .thenReturn("SG/08/0000000002")
                .thenReturn("SG/08/0000000003");

        mockMvc.perform(get("/api/v1/policy-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("SG/08/0000000001"));

        mockMvc.perform(get("/api/v1/policy-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("SG/08/0000000002"));

        mockMvc.perform(get("/api/v1/policy-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("SG/08/0000000003"));
    }

    @Test
    void testReturnNextPolicyNumber_EndpointExists() throws Exception {
        mockMvc.perform(get("/api/v1/policy-numbers"))
                .andExpect(status().isOk());
    }
}
