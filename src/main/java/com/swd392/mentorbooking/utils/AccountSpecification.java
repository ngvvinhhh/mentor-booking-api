package com.swd392.mentorbooking.utils;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.entity.Services;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AccountSpecification {

    public static Specification<Account> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Account> hasRole(RoleEnum role) {
        return (root, query, criteriaBuilder) ->
                role == null ? null : criteriaBuilder.equal(root.get("role"), role);
    }

    public static Specification<Account> hasAllSpecializations(List<SpecializationEnum> specializations) {
        return (root, query, criteriaBuilder) -> {
            if (specializations == null || specializations.isEmpty()) {
                return null;
            }

            // Join the specializations collection
            ListJoin<Account, SpecializationEnum> specializationJoin = root.joinList("specializations", JoinType.INNER);

            // Create a predicate to check if each specified specialization is in the account's specializations
            Predicate containsAllSpecializations = criteriaBuilder.and(
                    specializations.stream()
                            .map(specialization -> criteriaBuilder.isMember(specialization, root.get("specializations")))
                            .toArray(Predicate[]::new)
            );

            return containsAllSpecializations;
        };
    }

    public static Specification<Account> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }

            // Join Account with Service to access the price field
            Join<Account, Services> serviceJoin = root.join("service");

            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(serviceJoin.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(serviceJoin.get("price"), minPrice);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(serviceJoin.get("price"), maxPrice);
            }
        };
    }
}
