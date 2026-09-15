package com.danilodps.pay.domain.model;

import com.danilodps.pay.domain.model.entities.RoleEntity;

import java.util.List;

public interface RoleEntityRepository {

    List<RoleEntity> findRolesByProfileId(String profileId);

}
