package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.response.StoreResponse;

public interface StoreService {

    StoreResponse getById(String storeId);
    StoreResponse getByEmail(String storeEmail);
    StoreDTO update(String storeId, StoreResponse storeResponse);
    void delete(String storeId);
}
