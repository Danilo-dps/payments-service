package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.Store;

public class Store2StoreDTO {

    private Store2StoreDTO() {}

    public static StoreDTO convert(Store store){
        return new StoreDTO(store.getStoreId(),  store.getStoreName(), store.getCnpj(), store.getStoreEmail(), store.getPassword(), store.getRole(), store.getBalance());
    }
}
