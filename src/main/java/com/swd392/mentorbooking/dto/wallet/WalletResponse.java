package com.swd392.mentorbooking.dto.wallet;

import com.swd392.mentorbooking.entity.Account;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;

    private Double total;
}
