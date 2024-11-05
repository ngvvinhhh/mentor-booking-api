package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByEmail(String email);

    List<Account> findAccountsByRoleAndIsDeletedFalse(RoleEnum role);

    List<Account> findByIdIn(List<Long> accountIds);

    List<Account> findAccountsByIsDeletedFalse();

    Optional<Account> findFirstByEmail(String email);


    Account findAccountByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.isDeleted = false AND (:role IS NULL OR a.role = :role)")
    List<Account> findAccountsByRole(@Param("role") RoleEnum role);
}
