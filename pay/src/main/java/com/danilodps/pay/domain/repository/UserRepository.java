package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.DepositHistory;
import com.danilodps.pay.domain.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findAndLockByEmail(String email);
    Optional<User> findByCpf(String cpf);
    List<DepositHistory> findDepositByUserId(UUID userId);
    Optional<User> findByUsername(String username);
}
