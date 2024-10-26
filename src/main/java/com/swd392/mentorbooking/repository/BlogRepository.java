package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByAccount(Account account);

    List<Blog> findAllByIsDeletedFalse();

    List<Blog> findAllByAccountAndIsDeletedFalse(Account account);
}
