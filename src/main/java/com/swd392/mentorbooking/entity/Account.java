package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.*;
import com.swd392.mentorbooking.entity.Enum.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "day_of_birth", nullable = true)
    private LocalDateTime dayOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "phone", nullable = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleEnum role;

    @Column(name = "avatar", nullable = true)
    private String avatar;

    @Column(name = "class", nullable = true)
    private String className;

    @Column(name = "cv", nullable = true)
    private String cv;

    @JsonIgnore
    @ElementCollection(targetClass = SpecializationEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "account_specializations", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "specialization", nullable = true)
    private List<SpecializationEnum> specializations = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatusEnum status;

    @Column(name = "youtubeLink", nullable = true)
    private String youtubeLink;

    @Column(name = "linkedinLink", nullable = true)
    private String linkedinLink;

    @Column(name = "facebookLink", nullable = true)
    private String facebookLink;

    @Column(name = "twitterLink", nullable = true)
    private String twitterLink;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @JsonIgnore
    @JsonManagedReference
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Wallet wallet;

    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Services service;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<WebsiteFeedback> websiteFeedbacks;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Blog> blogs;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Achievement> achievements;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Topic> topics;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ProgressCard> progressCards;

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ServiceFeedback> feedbackRatings;

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role.name()));
        return authorities;
    }

    @JsonIgnore
    @Transient
    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    @Transient
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @JsonIgnore
    @Transient
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @JsonIgnore
    @Transient
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @JsonIgnore
    @Transient
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Transient
    private String tokens;

    @Transient
    private String refreshToken;

    public Account(String name, String email, RoleEnum role, AccountStatusEnum status) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.gender = GenderEnum.MALE;
    }
}