package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.repositories.JpaProfileEntityRepository;
import com.danilodps.pay.domain.model.ProfileEntityRepository;
import com.danilodps.pay.domain.model.entities.ProfileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfileEntityRepositoryImpl implements ProfileEntityRepository {

    private final JpaProfileEntityRepository jpaProfileEntityRepository;

    public ProfileEntityRepositoryImpl(JpaProfileEntityRepository jpaProfileEntityRepository) {
        this.jpaProfileEntityRepository = jpaProfileEntityRepository;
    }

    @Override
    public List<ProfileEntity> findAll() {
        return jpaProfileEntityRepository.findAll();
    }

    @Override
    public Optional<ProfileEntity> findById(String profileId) {
        return jpaProfileEntityRepository.findById(profileId);
    }

    @Override
    public ProfileEntity save(ProfileEntity profileEntity) {
        return this.jpaProfileEntityRepository.saveAndFlush(profileEntity);
    }

    @Override
    public void delete(ProfileEntity profileEntity) {
        this.jpaProfileEntityRepository.delete(profileEntity);
    }

    @Override
    public Optional<ProfileEntity> findByProfileEmail(String profileEmail) {
        return  this.jpaProfileEntityRepository.findByProfileEmail(profileEmail);
    }

    @Override
    public Optional<ProfileEntity> findAndLockByProfileEmail(String profileEmail) {
        return this.jpaProfileEntityRepository.findAndLockByProfileEmail(profileEmail);
    }

}
