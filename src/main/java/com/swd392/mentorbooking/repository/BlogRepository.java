package com.swd392.mentorbooking.repository;

import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByAccount(Account account);

    List<Blog> findAllByIsDeletedFalse();

    List<Blog> findAllByAccountAndIsDeletedFalse(Account account);

    @Query(value = "SELECT * FROM blog WHERE is_deleted = false ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<Blog> findRandomBlogs();

    @Query(value = "SELECT b.* " +
            "FROM railway.blog b " +
            "LEFT JOIN comment c ON b.blog_id = c.blog_id " +
            "GROUP BY b.blog_id " +
            "ORDER BY COUNT(c.comment_id) DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Blog> findBlogWithMostComments();

    List<Blog> findBlogsByBlogCategoryEnum(BlogCategoryEnum category);
}
