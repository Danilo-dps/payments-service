package com.danilodps.pay.domain.model.response.operations;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TransactionResponse(
        UUID transactionId,
        BigDecimal amount,
        String userSenderEmail,
        String receiverEmail,
        LocalDateTime transactionTimestamp){}