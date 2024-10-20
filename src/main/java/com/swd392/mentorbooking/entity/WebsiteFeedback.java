package com.swd392.mentorbooking.entity;

import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import com.swd392.mentorbooking.entity.Enum.WebsiteFeedbackEnum;
import lombok.*;
import jakarta.persistence.*;import java.time.LocalDateTime;

@Entity
@Table(name = "website_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebsiteFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type")
    private WebsiteFeedbackEnum websiteFeedbackEnum;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}

