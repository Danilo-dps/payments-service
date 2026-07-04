package com.danilodps.pay.domain.model;

import java.util.List;
import java.util.Optional;

public interface ProfileEntityRepository {

    List<ProfileEntity> findAll();
    Optional<ProfileEntity> findById(String profileId);
    ProfileEntity save(ProfileEntity profileEntity);
    void delete(ProfileEntity profileEntity);
    Optional<ProfileEntity> findByProfileEmail(String profileEmail);
    Optional<ProfileEntity> findAndLockByProfileEmail(String profileEmail);

}
