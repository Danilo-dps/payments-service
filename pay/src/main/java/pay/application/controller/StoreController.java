package pay.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pay.domain.dto.StoreDTO;
import pay.domain.record.ReceivedTransferResponse;
import pay.domain.record.StoreResponse;
import pay.domain.service.StoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService){
        this.storeService = storeService;
    }

    @GetMapping("/id/{storeId}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable UUID storeId) {
        StoreResponse storeSearch = storeService.getById(storeId);
        return ResponseEntity.ok(storeSearch);
    }

    @GetMapping("/email/{storeEmail}")
    public ResponseEntity<StoreResponse> getStoreByEmail(@PathVariable String storeEmail) {
        StoreResponse storeSearch = storeService.getByEmail(storeEmail);
        return ResponseEntity.ok(storeSearch);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable UUID storeId, @RequestBody StoreResponse storeResponse){
        StoreDTO storeUpdate = storeService.update(storeId, storeResponse);
        return ResponseEntity.ok(storeUpdate);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity <Void> deleteStore(@PathVariable UUID storeId){
        storeService.delete(storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transfer/{storeId}")
    public ResponseEntity<List<ReceivedTransferResponse>> getAllTransfer(@PathVariable UUID storeId){
        List<ReceivedTransferResponse> listAllTransfers = storeService.getAllReceivedTransfers(storeId);
        return listAllTransfers.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllTransfers);
    }
}
