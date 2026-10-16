package com.andres_lozada.demo.entity;

import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Risk Entity representing an insured risk in the insurance platform.
 * A risk can be covered by multiple policies (many-to-many relationship).
 */
@Entity
@Table(name = "risks", indexes = {
    @Index(name = "idx_risk_name", columnList = "risk_name", unique = true),
    @Index(name = "idx_risk_category", columnList = "risk_category"),
    @Index(name = "idx_risk_severity", columnList = "risk_severity"),
    @Index(name = "idx_risk_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "risk_id")
    private UUID riskId;

    @NotBlank(message = "Risk name is required")
    @Size(max = 255, message = "Risk name must not exceed 255 characters")
    @Column(name = "risk_name", unique = true, nullable = false, length = 255)
    private String riskName;

    @NotNull(message = "Risk category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", nullable = false, length = 50)
    private RiskCategory riskCategory;

    @NotNull(message = "Risk severity is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_severity", nullable = false, length = 20)
    private RiskSeverity riskSeverity;

    @ColumnDefault("'ACTIVE'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RiskStatus status = RiskStatus.ACTIVE;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "risks")
    @Builder.Default
    private Set<PolicyEntity> policies = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RiskStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiskEntity)) return false;
        return riskId != null && riskId.equals(((RiskEntity) o).riskId);
    }

    @Override
    public int hashCode() {
        return riskId != null ? riskId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RiskEntity{" +
                "riskId=" + riskId +
                ", riskName='" + riskName + '\'' +
                ", riskCategory=" + riskCategory +
                ", riskSeverity=" + riskSeverity +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
