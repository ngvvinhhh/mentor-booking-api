package com.swd392.mentorbooking.dto.walletlog;


import com.swd392.mentorbooking.entity.Enum.WalletLogType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletLogResponse {
    private long logId;
    private Double amount;
    private Long from;
    private Long to;
    private WalletLogType type;
    private LocalDateTime createdAt;

}
