package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.response.ProfileResponse;

public class ProfileEntity2ProfileResponse {

    private ProfileEntity2ProfileResponse() {}

    public static ProfileResponse convert(ProfileEntity profileEntity){
        return ProfileResponse.builder()
                .profileId(profileEntity.getProfileId())
                .username(profileEntity.getUsername())
                .email(profileEntity.getProfileEmail())
                .balance(profileEntity.getBalance())
                .build();
    }
}
