package com.danilodps.pay.domain.mappers.entities.core;

import com.danilodps.pay.adapters.outbound.entities.JpaProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileEntity2JpaProfileEntity {

    private ProfileEntity2JpaProfileEntity(){}

    public static JpaProfileEntity convert(ProfileEntity profileEntity){
        return new JpaProfileEntity(
                profileEntity.getProfileId(),
                profileEntity.getUsername(),
                profileEntity.getDocumentIdentifier(),
                profileEntity.getDocument(),
                profileEntity.getProfileEmail(),
                profileEntity.getPassword(),
                profileEntity.getBalance(),
                RoleEntity2JpaRoleEntity.convert(profileEntity.getRoles()),
                profileEntity.getCreatedAt(),
                profileEntity.getLastUpdated());
    }

    public static List<JpaProfileEntity> convert(List<ProfileEntity> profileEntities){
        return profileEntities.stream()
                .map(ProfileEntity2JpaProfileEntity::convert)
                .collect(Collectors.toList());
    }

}
