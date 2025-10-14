package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SentTransferResponse(UUID sendId, LocalDateTime whenDidItHappen, String destinationEmail, BigDecimal amount) {
}
