package com.danilodps.pay.domain.model;

import com.danilodps.pay.domain.model.entities.ProfileRoleEntity;

import java.util.List;

public interface ProfileRoleEntityRepository {

    ProfileRoleEntity save(ProfileRoleEntity profileRoleEntity);

    List<ProfileRoleEntity> findByIdProfileId(String profileId);

    void deleteByIdProfileId(String profileId);
}
