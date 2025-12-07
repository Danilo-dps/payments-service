package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.DepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepositEntityRepository extends JpaRepository<DepositEntity, UUID> {
}
