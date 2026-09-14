package com.danilodps.pay.adapters.inbound.controller.request.create.operations;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record DepositRequest(BigDecimal amount, String userEmail) {}