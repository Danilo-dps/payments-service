package com.danilodps.pay.domain.model.request.create;

import lombok.Builder;

@Builder
public record SignInRequest(String userEmail, String password) {}
