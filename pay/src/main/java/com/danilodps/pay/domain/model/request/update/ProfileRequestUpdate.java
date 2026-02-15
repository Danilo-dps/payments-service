package com.danilodps.pay.domain.model.request.update;

import lombok.Builder;

@Builder
public record ProfileRequestUpdate(String currentEmail, String newEmail, String currentPassword,String newPassword) {}
