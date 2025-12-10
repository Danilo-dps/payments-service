package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProfileResponse(
        UUID profileId,
        String username,
        String email,
        BigDecimal balance,
        LocalDateTime timestamp) { }
