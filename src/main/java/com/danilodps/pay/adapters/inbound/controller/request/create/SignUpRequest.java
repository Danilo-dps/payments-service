package com.danilodps.pay.adapters.inbound.controller.request.create;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignUpRequest(
        String username,
        String userEmail,
        String password,
        String documentIdentifier,
        String document,
        LocalDateTime signupTimestamp){}
