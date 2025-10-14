package com.danilodps.pay.domain.model.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignupRequest(String id, String username, String email, LocalDateTime now){}
