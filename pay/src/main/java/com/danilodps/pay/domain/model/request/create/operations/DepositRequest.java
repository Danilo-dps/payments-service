package com.danilodps.pay.domain.model.request.create.operations;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record DepositRequest(BigDecimal amount, String email) {}