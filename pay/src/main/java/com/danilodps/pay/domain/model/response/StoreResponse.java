package com.danilodps.pay.domain.model.response;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreResponse(String storeId, String storeName, String storeEmail, BigDecimal balance) {
}
