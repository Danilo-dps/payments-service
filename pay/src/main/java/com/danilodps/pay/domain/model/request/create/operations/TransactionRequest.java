package com.danilodps.pay.domain.model.request.create.operations;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionRequest(
        BigDecimal amount,
        String senderEmail,
        String receiverEmail,
        LocalDateTime transactionTimestamp) {}