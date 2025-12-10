package com.danilodps.pay.domain.model.response.operations;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DepositResponse(UUID depositId, String username, String email, BigDecimal amount, LocalDateTime depositTimestamp) {
}
