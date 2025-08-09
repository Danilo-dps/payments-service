package pay.domain.service;

import pay.domain.dto.StoreDTO;
import pay.domain.record.StoreResponse;
import pay.domain.record.TransferResponse;

import java.util.List;
import java.util.UUID;

public interface StoreService {

    StoreResponse getById(UUID storeId);
    StoreResponse getByEmail(String storeEmail);
    StoreDTO update(UUID storeId, StoreResponse storeResponse);
    void delete(UUID storeId);
    List<TransferResponse> getAllTransfers(UUID storeId);
}
