package com.danilodps.pay.domain.mappers;

import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.adapters.inbound.controller.response.ProfileResponse;

public class ProfileEntity2ProfileResponse {

    private ProfileEntity2ProfileResponse() {}

    public static ProfileResponse convert(ProfileEntity profileEntity){
        return ProfileResponse.builder()
                .profileId(profileEntity.getProfileId())
                .username(profileEntity.getUsername())
                .profileEmail(profileEntity.getProfileEmail())
                .balance(profileEntity.getBalance())
                .createdAt(profileEntity.getCreatedAt())
                .lastUpdated(profileEntity.getLastUpdated())
                .build();
    }
}
