package com.swd392.mentorbooking.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "semester")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    private Long id;

    @Column(name = "semester_name", nullable = false)
    private String semesterName;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}

