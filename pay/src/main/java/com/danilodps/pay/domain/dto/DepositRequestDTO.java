package com.danilodps.pay.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDTO {
    private BigDecimal amount;
    private String email;
}