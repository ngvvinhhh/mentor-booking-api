package com.swd392.mentorbooking.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "total", nullable = false)
    private Double total;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<WalletLog> walletLogs;
}

