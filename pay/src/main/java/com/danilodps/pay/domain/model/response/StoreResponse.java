package com.danilodps.pay.domain.model.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record StoreResponse(UUID storeId, String storeName, String storeEmail, BigDecimal balance) {
}
