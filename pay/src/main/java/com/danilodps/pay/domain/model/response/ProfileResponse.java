package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProfileResponse(
        String profileId,
        String username,
        String email,
        BigDecimal balance,
        LocalDateTime timestamp) { }
