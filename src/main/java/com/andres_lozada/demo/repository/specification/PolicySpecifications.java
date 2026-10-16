package com.andres_lozada.demo.repository.specification;

import com.andres_lozada.demo.entity.PolicyEntity;
import com.andres_lozada.demo.enums.PolicyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class PolicySpecifications {

    private PolicySpecifications() {
    }

    public static Specification<PolicyEntity> withFilters(UUID customerId, PolicyStatus status) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (customerId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("customerId"), customerId));
            }
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            return predicates;
        };
    }
}
