package com.swd392.mentorbooking.entity;

import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "day_of_birth")
    private LocalDateTime dayOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleEnum role;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "class")
    private String className;

    @ElementCollection(targetClass = SpecializationEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "account_specializations", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "specialization")
    private List<SpecializationEnum> specializations;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatusEnum status;

    @Column(name = "lecturer")
    private String lecturer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Service services;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<WebsiteFeedback> websiteFeedbacks;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Blog> blogs;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Achievement> achievements;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Topic> topics;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ServiceFeedback> feedbackRatings;
}