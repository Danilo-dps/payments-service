package pay.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pay.domain.model.Store;
import pay.domain.model.TransferHistory;
import pay.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findByStoreEmail(String storeEmail);
    Optional<Store> findByCnpj(String cnpj);
    Optional<Store> findByStoreName(String storeName);
    List<TransferHistory> findTransferByStoreId(UUID storeId);
    Boolean existsByStoreName(String storeName);
    Boolean existsByStoreEmail(String storeEmail);
}
