package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.domain.model.entities.ProfileRoleEntity;
import com.danilodps.pay.domain.model.entities.ProfileRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProfileRoleEntityRepository extends JpaRepository<ProfileRoleEntity, ProfileRoleId> {

    List<ProfileRoleEntity> findByIdProfileId(String profileId);

    @Modifying
    void deleteByIdProfileId(String profileId);
}
