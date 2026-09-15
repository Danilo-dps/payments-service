package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.domain.model.entities.ProfileEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProfileEntityRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByProfileEmail(String profileEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProfileEntity> findAndLockByProfileEmail(String profileEmail);

}
