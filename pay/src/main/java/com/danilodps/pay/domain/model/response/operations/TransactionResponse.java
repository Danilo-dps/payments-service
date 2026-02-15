package com.danilodps.pay.domain.model.response.operations;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponse(
        String transactionId,
        BigDecimal amount,
        String userSenderEmail,
        String receiverEmail,
        LocalDateTime transactionTimestamp){}