package com.danilodps.pay.domain.dto;

import com.danilodps.pay.domain.model.enums.EOperationType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DepositDTO {

    private UUID depositId;
    private LocalDateTime depositTimestamp;
    private EOperationType operationType;
    private BigDecimal amount;
    private UserDTO userDTO;

}