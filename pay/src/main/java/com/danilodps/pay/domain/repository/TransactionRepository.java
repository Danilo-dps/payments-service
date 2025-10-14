package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.Transaction;
import com.danilodps.pay.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByUserSender(User userSender);
    List<Transaction> findByUserReceiver(User userReceiver);
    List<Transaction> findByStoreReceiver(Store storeReceiver);

    @Query("SELECT t FROM Transaction t WHERE t.userSender = :user OR t.userReceiver = :user ORDER BY t.transactionTimestamp DESC")
    List<Transaction> findTransactionHistoryByUser(@Param("user") User user);
}
