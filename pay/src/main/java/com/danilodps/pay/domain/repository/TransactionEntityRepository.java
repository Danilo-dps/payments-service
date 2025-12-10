package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.ProfileEntity;
import com.danilodps.pay.domain.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByProfileSender(ProfileEntity profileSender);
    List<TransactionEntity> findByProfileReceiver(ProfileEntity profileReceiver);

    //TODO essa query é performática?
    @Query("SELECT t FROM TransactionEntity t WHERE t.profileSender = :profileEntity OR t.profileReceiver = :profileEntity ORDER BY t.transactionTimestamp DESC")
    List<TransactionEntity> findTransactionHistoryByProfileEntity(@Param("profileEntity") ProfileEntity pro);
}
