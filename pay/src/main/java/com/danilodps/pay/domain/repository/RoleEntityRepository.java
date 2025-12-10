package com.danilodps.pay.domain.repository;

import com.danilodps.pay.domain.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {}
