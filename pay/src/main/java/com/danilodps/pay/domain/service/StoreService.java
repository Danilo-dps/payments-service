package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.response.StoreResponse;

import java.util.UUID;

public interface StoreService {

    StoreResponse getById(UUID storeId);
    StoreResponse getByEmail(String storeEmail);
    StoreDTO update(UUID storeId, StoreResponse storeResponse);
    void delete(UUID storeId);
}
