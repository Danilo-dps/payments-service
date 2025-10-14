package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransferResponse(String transferId, String fullName, String fromEmail, String destinationEmail, BigDecimal amount, LocalDateTime whenDidItHappen) {
}
