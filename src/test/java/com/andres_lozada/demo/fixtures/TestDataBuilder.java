package com.andres_lozada.demo.fixtures;

import com.andres_lozada.demo.dto.CreateCustomerRequest;
import com.andres_lozada.demo.dto.CreatePolicyRequest;
import com.andres_lozada.demo.dto.CreateRiskRequest;
import com.andres_lozada.demo.entity.CustomerEntity;
import com.andres_lozada.demo.entity.PolicyEntity;
import com.andres_lozada.demo.entity.RiskEntity;
import com.andres_lozada.demo.enums.PolicyStatus;
import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class TestDataBuilder {
    public CustomerEntity buildCustomer() {
        return CustomerEntity.builder().customerId(UUID.randomUUID()).email("customer@example.com")
                .firstName("Test").lastName("Customer").build();
    }

    public CreateCustomerRequest buildCustomerRequest() {
        return CreateCustomerRequest.builder().email("customer@example.com")
                .firstName("Test").lastName("Customer").build();
    }

    public PolicyEntity buildPolicy(CustomerEntity customer) {
        return PolicyEntity.builder().policyId(UUID.randomUUID()).customerId(customer.getCustomerId())
                .customer(customer).policyType("AUTO").status(PolicyStatus.ACTIVE)
                .coverageAmount(new BigDecimal("25000.00")).premiumAmount(new BigDecimal("250.00"))
                .startDate(LocalDate.now().plusDays(1)).createdBy("test-user").build();
    }

    public CreatePolicyRequest buildPolicyRequest(UUID customerId) {
        return CreatePolicyRequest.builder().customerId(customerId).policyType("AUTO")
                .coverageAmount(new BigDecimal("25000.00")).premiumAmount(new BigDecimal("250.00"))
                .startDate(LocalDate.now().plusDays(1)).build();
    }

    public RiskEntity buildRisk() {
        return RiskEntity.builder().riskId(UUID.randomUUID()).riskName("Flood")
                .riskCategory(RiskCategory.FLOOD).riskSeverity(RiskSeverity.HIGH)
                .status(RiskStatus.ACTIVE).description("Flood exposure").build();
    }

    public CreateRiskRequest buildRiskRequest() {
        return CreateRiskRequest.builder().riskName("Flood").riskCategory(RiskCategory.FLOOD)
                .riskSeverity(RiskSeverity.HIGH).description("Flood exposure").build();
    }
}
