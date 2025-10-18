package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.DepositHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepositHistoryRepository extends JpaRepository<DepositHistory, UUID> {
}
