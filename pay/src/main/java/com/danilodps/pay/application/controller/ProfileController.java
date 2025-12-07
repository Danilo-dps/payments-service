package com.danilodps.pay.application.controller;

import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.operations.DepositResponse;
import com.danilodps.pay.domain.model.response.ProfileResponse;
import com.danilodps.pay.domain.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/id/{userId}")
    public ResponseEntity<ProfileResponse> getUserById(@PathVariable UUID userId) {
        ProfileResponse profileSearch = profileService.getById(userId);
        return ResponseEntity.ok(profileSearch);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ProfileResponse> getUserByEmail(@PathVariable String userEmail) {
        ProfileResponse profileSearch = profileService.getByEmail(userEmail);
        return ResponseEntity.ok(profileSearch);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileResponse> updateUser(@PathVariable UUID userId, @RequestBody ProfileRequestUpdate profileRequestUpdate){
        ProfileResponse profileSearch = profileService.update(userId, profileRequestUpdate);
        return ResponseEntity.ok(profileSearch);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity <Void> deleteUser(@PathVariable UUID userId){
        profileService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deposit/{userId}")
    public ResponseEntity<List<DepositResponse>> getAllDeposit(@PathVariable UUID userId){
        List<DepositResponse> listAllDeposits = profileService.getAllDeposits(userId);
        return listAllDeposits.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllDeposits);
    }

}
