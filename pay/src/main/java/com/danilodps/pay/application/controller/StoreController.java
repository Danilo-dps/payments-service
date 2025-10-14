package com.danilodps.pay.application.controller;

import com.danilodps.pay.domain.dto.StoreDTO;
import com.danilodps.pay.domain.model.response.StoreResponse;
import com.danilodps.pay.domain.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService){
        this.storeService = storeService;
    }

    @GetMapping("/id/{storeId}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable String storeId) {
        StoreResponse storeSearch = storeService.getById(storeId);
        return ResponseEntity.ok(storeSearch);
    }

    @GetMapping("/email/{storeEmail}")
    public ResponseEntity<StoreResponse> getStoreByEmail(@PathVariable String storeEmail) {
        StoreResponse storeSearch = storeService.getByEmail(storeEmail);
        return ResponseEntity.ok(storeSearch);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable String storeId, @RequestBody StoreResponse storeResponse){
        StoreDTO storeUpdate = storeService.update(storeId, storeResponse);
        return ResponseEntity.ok(storeUpdate);
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity <Void> deleteStore(@PathVariable String storeId){
        storeService.delete(storeId);
        return ResponseEntity.noContent().build();
    }

}
