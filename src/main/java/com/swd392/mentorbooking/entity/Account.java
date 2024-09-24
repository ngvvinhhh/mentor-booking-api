package com.swd392.mentorbooking.entity;

import com.swd392.mentorbooking.entity.Enum.AccountGenderEnum;
import com.swd392.mentorbooking.entity.Enum.AccountRoleEnum;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountGenderEnum gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatusEnum status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRoleEnum role;

    @Column(nullable = true)
    private String avatar;

    @Column(nullable = true)
    private String classes;

    @Column(nullable = true)
    private String lecture;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = true)
    private LocalDateTime updateAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}
