package com.danilodps.pay.adapters.inbound.controller.response;

import lombok.Builder;

import java.util.List;

@Builder
public record JwtResponse(
        String accessToken,
        String profileId,
        String username,
        String profileEmail,
        List<String> roles) {}
