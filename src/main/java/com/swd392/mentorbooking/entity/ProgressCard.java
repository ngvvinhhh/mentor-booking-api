package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "progress_cards") // Đổi tên bảng nếu cần
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    @JsonBackReference
    private ProgressColumn progressColumn; // Tham chiếu đến ProgressColumn

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description; // Mô tả của ProgressCard

    @Column(name = "cover", nullable = true)
    private String cover;

    @Column(name = "order_index") // Để xác định thứ tự của các card
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = true)
    @JsonIgnore
    private Account account;
}
