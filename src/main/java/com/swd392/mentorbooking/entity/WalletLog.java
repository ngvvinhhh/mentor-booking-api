package com.swd392.mentorbooking.entity;

import com.swd392.mentorbooking.entity.Enum.WalletLogType;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_log_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_log")
    private WalletLogType typeOfLog;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "from_account")
    private Long from;

    @Column(name = "to_account")
    private Long to;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

