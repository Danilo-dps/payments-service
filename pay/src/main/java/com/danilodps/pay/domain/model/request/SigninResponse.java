package com.danilodps.pay.domain.model.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SigninResponse(String id, String username, String email, LocalDateTime now){}
