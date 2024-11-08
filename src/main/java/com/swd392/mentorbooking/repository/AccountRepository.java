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

    Optional<Account> findByRole(RoleEnum role);

    // Tính tổng số account không bị xóa
    @Query("SELECT COUNT(a) FROM Account a WHERE a.isDeleted = false")
    long countActiveUsers();

    // Tính số lượng account có role và không bị xóa
    @Query("SELECT COUNT(a) FROM Account a WHERE a.role = :role AND a.isDeleted = false")
    long countByRoleAndNotDeleted(@Param("role") RoleEnum role);

    @Query("SELECT s, COUNT(a) FROM Account a JOIN a.specializations s WHERE a.isDeleted = false GROUP BY s")
    List<Object[]> countBySpecialization();


    @Query("SELECT a FROM Account a " +
            "JOIN a.wallet w " + // Kết nối với Wallet thông qua mối quan hệ
            "WHERE a.role = :role AND a.isDeleted = false " +
            "ORDER BY w.total DESC") // Sắp xếp theo total trong Wallet
    List<Account> findStudentsOrderedByTotalPayments(@Param("role") RoleEnum role);

    Account findAccountByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.isDeleted = false AND (:role IS NULL OR a.role = :role)")
    List<Account> findAccountsByRole(@Param("role") RoleEnum role);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.isDeleted = false")
    Optional<Integer> countTotalUsers();

    @Query("SELECT COUNT(a) FROM Account a WHERE a.isDeleted = false AND a.role = 'STUDENT'")
    Optional<Integer> countTotalStudents();

    @Query("SELECT COUNT(a) FROM Account a WHERE a.isDeleted = false AND a.role = 'MENTOR'")
    Optional<Integer> countTotalMentors();
}
