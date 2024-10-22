package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    @JsonIgnore
    private Blog blog;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "description", nullable = false)
    private String description;
}
