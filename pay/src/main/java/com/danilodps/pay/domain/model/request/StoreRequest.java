package com.danilodps.pay.domain.model.request;

import lombok.Builder;

@Builder
public record StoreRequest(String storeName, String storeEmail, String password) { }
