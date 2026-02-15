package com.danilodps.pay.domain.model.request.create;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignUpRequest(
        String username,
        String email,
        String password,
        String documentIdentifier,
        String document,
        LocalDateTime signupTimestamp){}
