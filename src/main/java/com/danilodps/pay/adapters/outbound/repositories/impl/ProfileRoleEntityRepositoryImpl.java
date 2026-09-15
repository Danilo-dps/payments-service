package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.repositories.JpaProfileRoleEntityRepository;
import com.danilodps.pay.domain.model.ProfileRoleEntityRepository;
import com.danilodps.pay.domain.model.entities.ProfileRoleEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileRoleEntityRepositoryImpl implements ProfileRoleEntityRepository {

    private final JpaProfileRoleEntityRepository jpaProfileRoleEntityRepository;

    public ProfileRoleEntityRepositoryImpl(JpaProfileRoleEntityRepository jpaProfileRoleEntityRepository) {
        this.jpaProfileRoleEntityRepository = jpaProfileRoleEntityRepository;
    }

    @Override
    public ProfileRoleEntity save(ProfileRoleEntity profileRoleEntity) {
        return jpaProfileRoleEntityRepository.saveAndFlush(profileRoleEntity);
    }

    @Override
    public List<ProfileRoleEntity> findByIdProfileId(String profileId) {
        return jpaProfileRoleEntityRepository.findByIdProfileId(profileId);
    }

    @Override
    public void deleteByIdProfileId(String profileId) {
        jpaProfileRoleEntityRepository.deleteByIdProfileId(profileId);
    }

}
