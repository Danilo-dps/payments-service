package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.adapters.outbound.entities.JpaProfileEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProfileEntityRepository extends JpaRepository<JpaProfileEntity, String> {

    Optional<JpaProfileEntity> findByProfileEmail(String profileEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<JpaProfileEntity> findAndLockByProfileEmail(String profileEmail);

}
