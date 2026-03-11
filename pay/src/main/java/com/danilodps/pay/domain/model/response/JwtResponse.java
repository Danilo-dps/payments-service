package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public record JwtResponse(
        String accessToken, // futuramente, não vou retornar, apenas para testar
        String profileId,
        String username,
        String profileEmail,
        List<String> roles) {}
