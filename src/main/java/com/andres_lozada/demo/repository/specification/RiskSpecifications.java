package com.andres_lozada.demo.repository.specification;

import com.andres_lozada.demo.entity.RiskEntity;
import com.andres_lozada.demo.enums.RiskCategory;
import com.andres_lozada.demo.enums.RiskSeverity;
import com.andres_lozada.demo.enums.RiskStatus;
import org.springframework.data.jpa.domain.Specification;

public final class RiskSpecifications {

    private RiskSpecifications() {
    }

    public static Specification<RiskEntity> withFilters(
            RiskCategory riskCategory,
            RiskSeverity riskSeverity,
            RiskStatus status) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (riskCategory != null) {
                predicates = cb.and(predicates, cb.equal(root.get("riskCategory"), riskCategory));
            }
            if (riskSeverity != null) {
                predicates = cb.and(predicates, cb.equal(root.get("riskSeverity"), riskSeverity));
            }
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            return predicates;
        };
    }
}
