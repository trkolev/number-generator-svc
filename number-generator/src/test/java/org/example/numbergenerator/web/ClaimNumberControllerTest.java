package org.example.numbergenerator.web;

import org.example.numbergenerator.claim.service.ClaimNumberGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClaimNumberController.class)
class ClaimNumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimNumberGeneratorService claimNumberGeneratorService;

    @Test
    void testReturnNextClaimNumber_ReturnsOk() throws Exception {
        String expectedNumber = "Cl-0000000001";
        when(claimNumberGeneratorService.generateNextClaimNumber()).thenReturn(expectedNumber);

        mockMvc.perform(get("/api/v1/claim-numbers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(expectedNumber));
    }

    @Test
    void testReturnNextClaimNumber_ReturnsCorrectFormat() throws Exception {
        String expectedNumber = "Cl-0000000123";
        when(claimNumberGeneratorService.generateNextClaimNumber()).thenReturn(expectedNumber);

        mockMvc.perform(get("/api/v1/claim-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cl-0000000123"));
    }

    @Test
    void testReturnNextClaimNumber_MultipleCalls_ReturnsDifferentNumbers() throws Exception {
        when(claimNumberGeneratorService.generateNextClaimNumber())
                .thenReturn("Cl-0000000001")
                .thenReturn("Cl-0000000002")
                .thenReturn("Cl-0000000003");

        mockMvc.perform(get("/api/v1/claim-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cl-0000000001"));

        mockMvc.perform(get("/api/v1/claim-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cl-0000000002"));

        mockMvc.perform(get("/api/v1/claim-numbers"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cl-0000000003"));
    }

    @Test
    void testReturnNextClaimNumber_EndpointExists() throws Exception {
        mockMvc.perform(get("/api/v1/claim-numbers"))
                .andExpect(status().isOk());
    }
}
