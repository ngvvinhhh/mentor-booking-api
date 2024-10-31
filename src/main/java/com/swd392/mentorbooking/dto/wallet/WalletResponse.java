package com.swd392.mentorbooking.dto.wallet;


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
