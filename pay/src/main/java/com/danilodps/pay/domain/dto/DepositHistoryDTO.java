package com.danilodps.pay.domain.dto;

import com.danilodps.pay.domain.model.enums.EOperationType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DepositHistoryDTO {

    private String depositId;
    private LocalDateTime whenDidItHappen;
    private EOperationType operationType;
    private BigDecimal amount;
    private UserDTO userDTO;

    public DepositHistoryDTO(LocalDateTime whenDidItHappen, EOperationType operationType, BigDecimal amount, UserDTO userDTO) {
        this.whenDidItHappen = whenDidItHappen;
        this.operationType = operationType;
        this.amount = amount;
        this.userDTO = userDTO;
    }
}