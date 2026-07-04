package com.danilodps.pay.domain.mappers.entities.jpa;

import com.danilodps.pay.adapters.outbound.entities.JpaProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntity;

import java.util.List;
import java.util.stream.Collectors;

public class JpaProfileEntity2ProfileEntity {

    private JpaProfileEntity2ProfileEntity(){}

    public static ProfileEntity convert(JpaProfileEntity jpaProfileEntity){
        return new ProfileEntity(
                jpaProfileEntity.getProfileId(),
                jpaProfileEntity.getUsername(),
                jpaProfileEntity.getDocumentIdentifier(),
                jpaProfileEntity.getDocument(),
                jpaProfileEntity.getProfileEmail(),
                jpaProfileEntity.getPassword(),
                jpaProfileEntity.getBalance(),
                JpaRoleEntity2RoleEntity.convert(jpaProfileEntity.getRoles()),
                jpaProfileEntity.getCreatedAt(),
                jpaProfileEntity.getLastUpdated());
    }

    public static List<ProfileEntity> convert(List<JpaProfileEntity> jpaProfileEntities){
        return jpaProfileEntities.stream()
                .map(JpaProfileEntity2ProfileEntity::convert)
                .collect(Collectors.toList());
    }

}
