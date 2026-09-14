package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.adapters.outbound.entities.JpaTransactionEntity;
import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransactionEntityRepository extends JpaRepository<JpaTransactionEntity, String> {

    JpaTransactionEntity findByTransactionId(String transactionId);

    @Query("SELECT t.transactionId as transactionId, " +
            "t.profileReceiver.profileId as profileReceiver, " +
            "t.transactionAt as transactionAt, " +
            "t.amount as amount " +
            "FROM JpaTransactionEntity t " +
            "WHERE t.profileSender.profileId = :profileId")
    List<TransactionProjection> findTransactionsByProfileId(@Param("profileId") String profileId);
}
