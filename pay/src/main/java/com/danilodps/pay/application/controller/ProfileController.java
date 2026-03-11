package com.danilodps.pay.application.controller;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.request.update.ProfileRequestUpdate;
import com.danilodps.pay.domain.model.response.ProfileResponse;
import com.danilodps.pay.domain.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/id/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<ProfileResponse> getById(@PathVariable String profileId) {
        ProfileResponse profileSearch = profileService.getById(profileId);
        return ResponseEntity.ok(profileSearch);
    }

    @GetMapping("/profileEmail/{profileEmail}")
    @PreAuthorize("#profileEmail == authentication.principal.profileEmail && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<ProfileResponse> getByEmail(@PathVariable String profileEmail) {
        ProfileResponse profileSearch = profileService.getByEmail(profileEmail);
        return ResponseEntity.ok(profileSearch);
    }

    @PutMapping("/update/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<ProfileResponse> update(@PathVariable String profileId, @RequestBody ProfileRequestUpdate profileRequestUpdate){
        ProfileResponse profileSearch = profileService.update(profileId, profileRequestUpdate);
        return ResponseEntity.ok(profileSearch);
    }

    @DeleteMapping("/delete/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<Void> delete(@PathVariable String profileId){
        profileService.delete(profileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deposit/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER')")
    public ResponseEntity<List<DepositResponse>> getAllDeposit(@PathVariable String profileId){
        List<DepositResponse> listAllDeposits = profileService.getAllDeposits(profileId);
        return listAllDeposits.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(listAllDeposits);
    }

}
