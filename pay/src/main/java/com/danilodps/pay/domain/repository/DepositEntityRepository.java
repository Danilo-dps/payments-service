package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.DepositEntity;
import com.danilodps.pay.domain.repository.projection.DepositProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositEntityRepository extends JpaRepository<DepositEntity, String> {

    @Query("SELECT d.depositId as depositId, d.depositAt as depositAt, d.amount as amount " +
            "FROM DepositEntity d " +
            "WHERE d.profileEntity.profileId = :profileId")
    List<DepositProjection> findDepositsByProfileId(@Param("profileId") String profileId);
}
