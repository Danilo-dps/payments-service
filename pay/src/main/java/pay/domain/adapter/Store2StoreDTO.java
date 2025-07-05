package pay.domain.adapter;

import pay.domain.dto.StoreDTO;
import pay.domain.model.Store;

public class Store2StoreDTO {

    private Store2StoreDTO() {}

    public static StoreDTO convert(Store store){
        return new StoreDTO(store.getStoreId(),  store.getStoreName(), store.getCnpj(), store.getStoreEmail(), store.getBalance());
    }
}
