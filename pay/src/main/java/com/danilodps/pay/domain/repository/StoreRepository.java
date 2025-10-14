package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    Optional<Store> findByStoreEmail(String storeEmail);
    Optional<Store> findByCnpj(String cnpj);
    Optional<Store> findByStoreName(String storeName);
    Boolean existsByStoreName(String storeName);
    Boolean existsByStoreEmail(String storeEmail);
}
