package com.danilodps.pay.domain.model.request.create.operations;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequest(
        String senderEmail,
        String receiverEmail,
        BigDecimal amount) {}