package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.adapters.outbound.entities.JpaRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRoleEntityRepository extends JpaRepository<JpaRoleEntity, Long> {}
