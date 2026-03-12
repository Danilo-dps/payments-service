package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.TransactionEntity;
import com.danilodps.pay.domain.repository.projection.TransactionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, String> {

    @Query("SELECT t.transactionId as transactionId, " +
            "t.profileReceiver.profileId as profileReceiver, " +
            "t.transactionAt as transactionAt, " +
            "t.amount as amount " +
            "FROM TransactionEntity t " +
            "WHERE t.profileSender.profileId = :profileId")
    List<TransactionProjection> findTransactionsByProfileId(@Param("profileId") String profileId);
}
