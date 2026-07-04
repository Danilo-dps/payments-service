package com.danilodps.pay.adapters.inbound.controller;

import com.danilodps.pay.adapters.inbound.controller.request.update.ProfileRequestUpdate;
import com.danilodps.pay.adapters.inbound.controller.response.ProfileResponse;
import com.danilodps.pay.application.usecases.ProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/v1")
public class ProfileController {

    private final ProfileUseCase profileUseCase;

    @GetMapping("/id/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<ProfileResponse> getById(@PathVariable String profileId) {
        ProfileResponse profileSearch = profileUseCase.getById(profileId);
        return ResponseEntity.ok(profileSearch);
    }

    @GetMapping("/profileEmail/{profileEmail}")
    @PreAuthorize("#profileEmail == authentication.principal.profileEmail && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<ProfileResponse> getByEmail(@PathVariable String profileEmail) {
        ProfileResponse profileSearch = profileUseCase.getByEmail(profileEmail);
        return ResponseEntity.ok(profileSearch);
    }

    @PutMapping("/update/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<ProfileResponse> update(@PathVariable String profileId, @RequestBody ProfileRequestUpdate profileRequestUpdate){
        ProfileResponse profileSearch = profileUseCase.update(profileId, profileRequestUpdate);
        return ResponseEntity.ok(profileSearch);
    }

    @DeleteMapping("/delete/{profileId}")
    @PreAuthorize("#profileId == authentication.principal.profileId && hasAnyAuthority('USER', 'COMPANY')")
    public ResponseEntity<Void> delete(@PathVariable String profileId){
        profileUseCase.delete(profileId);
        return ResponseEntity.noContent().build();
    }

}
