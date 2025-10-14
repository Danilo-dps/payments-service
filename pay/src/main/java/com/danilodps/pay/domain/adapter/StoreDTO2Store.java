package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.Store;

public class StoreDTO2Store {

    private StoreDTO2Store() {}

    public static Store convert(StoreDTO storeDTO){
        return new Store(storeDTO.getStoreId(),  storeDTO.getStoreName(), storeDTO.getCnpj(), storeDTO.getStoreEmail(), storeDTO.getPassword(), storeDTO.getRole(), storeDTO.getBalance());
    }
}
