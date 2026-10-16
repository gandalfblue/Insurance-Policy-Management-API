package com.andres_lozada.demo;

import com.andres_lozada.demo.dto.CustomerResponse;
import com.andres_lozada.demo.dto.PolicyResponse;
import com.andres_lozada.demo.dto.RiskResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void apiRequiresKeyAndReturnsPaginatedRisks() throws Exception {
        mockMvc.perform(get("/api/risks"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/risks").header("x-api-key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.pagination.totalCount").exists());
    }

    @Test
    void createsCustomerAndPolicyThroughHttpApi() throws Exception {
        MvcResult customerResult = mockMvc.perform(post("/api/customers")
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"integration@example.com","firstName":"Integration","lastName":"Test"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        CustomerResponse customer = objectMapper.readValue(
                customerResult.getResponse().getContentAsString(), CustomerResponse.class);
        mockMvc.perform(get("/api/customers/" + customer.getCustomerId()).header("x-api-key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("integration@example.com"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                                "/api/customers/" + customer.getCustomerId())
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));

        MvcResult policyResult = mockMvc.perform(post("/api/policies")
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "customerId", customer.getCustomerId(),
                                "policyType", "AUTO",
                                "coverageAmount", 25000,
                                "premiumAmount", 250,
                                "startDate", LocalDate.now().plusDays(1).toString()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();
        PolicyResponse policy = objectMapper.readValue(
                policyResult.getResponse().getContentAsString(), PolicyResponse.class);
        mockMvc.perform(get("/api/policies/" + policy.getPolicyId()).header("x-api-key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(policy.getPolicyId().toString()));
        mockMvc.perform(get("/api/policies").header("x-api-key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.totalCount").value(1));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                                "/api/policies/" + policy.getPolicyId())
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated policy\",\"coverageAmount\":30000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated policy"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/policies/" + policy.getPolicyId()).header("x-api-key", "test-api-key"))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsBadRequestForInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/risks").header("x-api-key", "test-api-key").param("pageSize", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void coreIntegrationMockSupportsSyncAndQuery() throws Exception {
        mockMvc.perform(post("/api/core/sync")
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"syncType\":\"ALL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(get("/api/core/query").header("x-api-key", "test-api-key").param("type", "POLICIES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("POLICIES"));
    }

    @Test
    void coreMockProcessesSyncEvents() throws Exception {
        mockMvc.perform(post("/core-mock/evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"SYNC\",\"requestId\":\"00000000-0000-0000-0000-000000000001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void coreMockProcessesQueriesAndPolicyEvents() throws Exception {
        mockMvc.perform(post("/core-mock/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"QUERY\",\"queryType\":\"POLICIES\",\"criteria\":{}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(post("/core-mock/evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"POLICY_UPDATED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSED"));
    }

    @Test
    void invalidCustomerRequestReturnsFieldErrorsAndMissingPolicyReturns404() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid\",\"firstName\":\"\",\"lastName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
        mockMvc.perform(get("/api/policies/00000000-0000-0000-0000-000000000001")
                        .header("x-api-key", "test-api-key"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POLICY_NOT_FOUND"));
    }

    @Test
    void riskApiCreatesAndListsRisk() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/risks")
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"riskName":"Wind","riskCategory":"WINDSTORM","riskSeverity":"HIGH","description":"Wind exposure"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        RiskResponse risk = objectMapper.readValue(result.getResponse().getContentAsString(), RiskResponse.class);
        mockMvc.perform(get("/api/risks/" + risk.getRiskId()).header("x-api-key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskName").value("Wind"));
        mockMvc.perform(get("/api/risks").header("x-api-key", "test-api-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.totalCount").value(1));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                                "/api/risks/" + risk.getRiskId())
                        .header("x-api-key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated wind exposure\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated wind exposure"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(
                        "/api/risks/" + risk.getRiskId()).header("x-api-key", "test-api-key"))
                .andExpect(status().isNoContent());
    }
}
