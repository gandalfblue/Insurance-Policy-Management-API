package com.andres_lozada.demo.entity;

import com.andres_lozada.demo.enums.PolicyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Policy Entity representing an insurance policy in the platform.
 * A policy belongs to one customer (many-to-one) and can cover multiple risks (many-to-many).
 */
@Entity
@Table(name = "policies", indexes = {
    @Index(name = "idx_policy_customer_id", columnList = "customer_id"),
    @Index(name = "idx_policy_status", columnList = "status"),
    @Index(name = "idx_policy_created_at", columnList = "created_at"),
    @Index(name = "idx_policy_customer_status", columnList = "customer_id, status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "policy_id")
    private UUID policyId;

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private CustomerEntity customer;

    @NotBlank(message = "Policy type is required")
    @Size(max = 50, message = "Policy type must not exceed 50 characters")
    @Column(name = "policy_type", nullable = false, length = 50)
    private String policyType;

    @ColumnDefault("'ACTIVE'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PolicyStatus status = PolicyStatus.ACTIVE;

    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Coverage amount must be greater than zero")
    @Column(name = "coverage_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal coverageAmount;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.01", inclusive = false, message = "Premium amount must be greater than zero")
    @Column(name = "premium_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumAmount;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotBlank(message = "Created by is required")
    @Size(max = 100, message = "Created by must not exceed 100 characters")
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Size(max = 100, message = "Updated by must not exceed 100 characters")
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "policy_risk_mapping",
        joinColumns = @JoinColumn(name = "policy_id"),
        inverseJoinColumns = @JoinColumn(name = "risk_id")
    )
    @Builder.Default
    private Set<RiskEntity> risks = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PolicyStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyEntity)) return false;
        return policyId != null && policyId.equals(((PolicyEntity) o).policyId);
    }

    @Override
    public int hashCode() {
        return policyId != null ? policyId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PolicyEntity{" +
                "policyId=" + policyId +
                ", customerId=" + customerId +
                ", policyType='" + policyType + '\'' +
                ", status=" + status +
                ", coverageAmount=" + coverageAmount +
                ", premiumAmount=" + premiumAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", risks=" + risks +
                '}';
    }
}
