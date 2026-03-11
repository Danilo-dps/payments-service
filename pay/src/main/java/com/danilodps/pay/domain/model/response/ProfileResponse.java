package com.danilodps.pay.domain.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileResponse(
        String profileId,
        String username,
        String profileEmail,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime lastUpdated) { }
