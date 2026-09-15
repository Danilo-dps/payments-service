package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.adapters.outbound.repositories.projection.TransactionProjection;
import com.danilodps.pay.domain.model.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransactionEntityRepository extends JpaRepository<TransactionEntity, String> {

    TransactionEntity findByTransactionId(String transactionId);

    @Query("SELECT t.transactionId as transactionId, " +
            "t.receiverProfileId as profileReceiver, " +
            "t.transactionAt as transactionAt, " +
            "t.amount as amount " +
            "FROM TransactionEntity t " +
            "WHERE t.senderProfileId = :profileId")
    List<TransactionProjection> findTransactionsByProfileId(@Param("profileId") String profileId);
}