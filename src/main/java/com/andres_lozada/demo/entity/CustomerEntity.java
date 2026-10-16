package com.andres_lozada.demo.entity;

import com.andres_lozada.demo.enums.PolicyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
 * Customer Entity representing a policyholder in the insurance platform.
 * A customer can have multiple insurance policies.
 */
@Entity
@Table(name = "customers", 
       indexes = {
           @Index(name = "idx_customer_email", columnList = "email"),
           @Index(name = "idx_customer_created_at", columnList = "created_at")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_id")
    private UUID customerId;

    @Email
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotNull
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PolicyEntity> policies = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Adds a policy to this customer.
     * 
     * @param policy the policy to add
     */
    public void addPolicy(PolicyEntity policy) {
        this.policies.add(policy);
        policy.setCustomer(this);
    }

    /**
     * Removes a policy from this customer.
     * 
     * @param policy the policy to remove
     */
    public void removePolicy(PolicyEntity policy) {
        this.policies.remove(policy);
        policy.setCustomer(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerEntity)) return false;
        return customerId != null && customerId.equals(((CustomerEntity) o).customerId);
    }

    @Override
    public int hashCode() {
        return customerId != null ? customerId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CustomerEntity{" +
                "customerId=" + customerId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
