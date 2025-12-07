package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SignUpResponse(UUID id, String username, String email, LocalDateTime timestamp){}
