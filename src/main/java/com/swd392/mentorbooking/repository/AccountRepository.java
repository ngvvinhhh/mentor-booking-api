package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByEmail(String email);

    List<Account> findAccountsByRole(RoleEnum role);

    List<Account> findByIdIn(List<Long> accountIds);


    List<Account> findAccountsByRoleAndServicePriceBetweenOrderByServicePriceAsc(RoleEnum roleEnum, double minPrice, double maxPrice);

    List<Account> findAccountsByRoleAndServicePriceBetweenOrderByServicePriceDesc(RoleEnum roleEnum, double minPrice, double maxPrice);
}
