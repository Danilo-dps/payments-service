package pay.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pay.domain.model.Store;
import pay.domain.model.Transaction;
import pay.domain.model.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserSender(User userSender);
    List<Transaction> findByUserReceiver(User userReceiver);
    List<Transaction> findByStoreReceiver(Store storeReceiver);

    @Query("SELECT t FROM Transaction t WHERE t.userSender = :user OR t.userReceiver = :user ORDER BY t.transactionTimestamp DESC")
    List<Transaction> findTransactionHistoryByUser(@Param("user") User user);
}
