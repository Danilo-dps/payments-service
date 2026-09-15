package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.repositories.JpaRoleEntityRepository;
import com.danilodps.pay.domain.model.RoleEntityRepository;
import com.danilodps.pay.domain.model.entities.RoleEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleEntityRepositoryImpl implements RoleEntityRepository {

    private final JpaRoleEntityRepository jpaRoleEntityRepository;

    public RoleEntityRepositoryImpl(JpaRoleEntityRepository jpaRoleEntityRepository) {
        this.jpaRoleEntityRepository = jpaRoleEntityRepository;
    }

    @Override
    public List<RoleEntity> findRolesByProfileId(String profileId) {
        return jpaRoleEntityRepository.findRolesByProfileId(profileId);
    }

}
