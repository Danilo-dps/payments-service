package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.adapters.outbound.entities.JpaDepositEntity;
import com.danilodps.pay.adapters.outbound.repositories.projection.DepositProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaDepositEntityRepository extends JpaRepository<JpaDepositEntity, String> {

    @Query("SELECT d.depositId as depositId, d.depositAt as depositAt, d.amount as amount " +
            "FROM JpaDepositEntity d " +
            "WHERE d.profileEntity.profileId = :profileId")
    List<DepositProjection> findDepositsByProfileId(@Param("profileId") String profileId);
}
