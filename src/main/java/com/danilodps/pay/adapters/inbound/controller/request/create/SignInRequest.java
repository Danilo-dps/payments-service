package com.danilodps.pay.adapters.inbound.controller.request.create;

import lombok.Builder;

@Builder
public record SignInRequest(String userEmail, String password) {}
