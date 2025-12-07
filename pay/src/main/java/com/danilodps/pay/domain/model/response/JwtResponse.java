package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record JwtResponse(
        String accessToken,
        UUID id,
        String username,
        String email,
        List<String> roles) {}
