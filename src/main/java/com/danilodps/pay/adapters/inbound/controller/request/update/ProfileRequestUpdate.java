package com.danilodps.pay.adapters.inbound.controller.request.update;

import lombok.Builder;

@Builder
public record ProfileRequestUpdate(String currentEmail, String newEmail, String currentPassword, String newPassword) {}
