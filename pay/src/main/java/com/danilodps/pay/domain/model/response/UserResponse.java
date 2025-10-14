package com.danilodps.pay.domain.model.response;

import java.math.BigDecimal;

public record UserResponse(String userId, String username, String email, BigDecimal balance) {
}
