package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignInResponse(String id, String username, String email, LocalDateTime timestamp){}
