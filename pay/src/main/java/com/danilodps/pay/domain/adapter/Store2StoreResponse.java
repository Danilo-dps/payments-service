package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.response.StoreResponse;

public class Store2StoreResponse {

    private Store2StoreResponse() {}

    public static StoreResponse convert(Store store){
        return new StoreResponse(store.getStoreId(), store.getStoreName(), store.getStoreEmail(), store.getBalance());
    }
}
