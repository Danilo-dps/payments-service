package com.danilodps.pay.adapters.outbound.repositories.impl;

import com.danilodps.pay.adapters.outbound.entities.JpaProfileEntity;
import com.danilodps.pay.adapters.outbound.repositories.JpaProfileEntityRepository;
import com.danilodps.pay.domain.mappers.entities.JpaProfileEntity2ProfileEntity;
import com.danilodps.pay.domain.mappers.entities.ProfileEntity2JpaProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.ProfileEntityRepository;
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
        List<JpaProfileEntity> jpaProfileEntities = jpaProfileEntityRepository.findAll();
        if(!jpaProfileEntities.isEmpty()){
            return JpaProfileEntity2ProfileEntity.convert(jpaProfileEntities);
        }
        return List.of();
    }

    @Override
    public Optional<ProfileEntity> findById(String profileId) {
        Optional<JpaProfileEntity>  jpaProfileEntity = jpaProfileEntityRepository.findById(profileId);
        return jpaProfileEntity.map(profileEntity -> Optional.of(JpaProfileEntity2ProfileEntity.convert(profileEntity))).orElse(null);
    }

    @Override
    public ProfileEntity save(ProfileEntity profileEntity) {
        JpaProfileEntity  jpaProfileEntity =
                this.jpaProfileEntityRepository.saveAndFlush(ProfileEntity2JpaProfileEntity.convert(profileEntity));
        return JpaProfileEntity2ProfileEntity.convert(jpaProfileEntity);
    }

    @Override
    public void delete(ProfileEntity profileEntity) {
        this.jpaProfileEntityRepository.delete(ProfileEntity2JpaProfileEntity.convert(profileEntity));
    }

    @Override
    public Optional<ProfileEntity> findByProfileEmail(String profileEmail) {
        Optional<JpaProfileEntity> optJpaProfileEntity = this.jpaProfileEntityRepository.findByProfileEmail(profileEmail);
        return optJpaProfileEntity.map(JpaProfileEntity2ProfileEntity::convert).or(Optional::empty);
    }

    @Override
    public Optional<ProfileEntity> findAndLockByProfileEmail(String profileEmail) {
        Optional<JpaProfileEntity> optJpaProfileEntity = this.jpaProfileEntityRepository.findAndLockByProfileEmail(profileEmail);
        return optJpaProfileEntity.map(JpaProfileEntity2ProfileEntity::convert).or(Optional::empty);
    }

}
