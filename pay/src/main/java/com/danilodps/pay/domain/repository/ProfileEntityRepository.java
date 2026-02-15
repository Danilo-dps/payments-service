package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.ProfileEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileEntityRepository extends JpaRepository<ProfileEntity, String> {
    Optional<ProfileEntity> findByProfileEmail(String profileEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProfileEntity> findAndLockByProfileEmail(String profileEmail);
    Optional<ProfileEntity> findByUsername(String username);
}
