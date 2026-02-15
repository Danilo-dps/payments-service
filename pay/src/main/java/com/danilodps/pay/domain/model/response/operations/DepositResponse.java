package com.danilodps.pay.domain.model.response.operations;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DepositResponse(String depositId, String username, String email, BigDecimal amount, LocalDateTime depositTimestamp) {
}
