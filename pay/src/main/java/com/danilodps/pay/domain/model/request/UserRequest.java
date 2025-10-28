package com.danilodps.pay.domain.model.request;

import lombok.Builder;

@Builder
public record UserRequest(String username, String email, String password) {
}
