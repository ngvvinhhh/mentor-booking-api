package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "progress_columns")  // Đổi tên bảng nếu cần
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "progress_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private ProjectProgress projectProgress; // Tham chiếu đến ProjectProgress

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "order_index") // Để xác định thứ tự của các column
    private Integer orderIndex;

    @JsonIgnore
    @OneToMany(mappedBy = "progressColumn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgressCard> progressCards; // Liên kết với nhiều ProgressCard
}
