package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private Account account;

    @Column(name = "image")
    private String image;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private BlogCategoryEnum blogCategoryEnum;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private List<Comment> comments;
}

