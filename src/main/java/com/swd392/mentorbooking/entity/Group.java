package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = true)
    private Topic topic;

    @Column(name = "quantity_member")
    private Integer quantityMember;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProjectProgress> projectProgresses;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Account> students;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Invitation> invitations;

}

