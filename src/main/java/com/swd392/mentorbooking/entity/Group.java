package com.swd392.mentorbooking.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "student_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "quantity_member")
    private Integer quantityMember;

    @ElementCollection
    @CollectionTable(name = "group_students", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "account_id")
    private List<Long> students;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<ProjectProgress> projectProgresses;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Account> accounts;
}

