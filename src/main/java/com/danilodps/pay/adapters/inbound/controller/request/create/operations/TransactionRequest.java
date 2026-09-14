package com.danilodps.pay.adapters.inbound.controller.request.create.operations;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequest(
        String senderEmail,
        String receiverEmail,
        BigDecimal amount) {}