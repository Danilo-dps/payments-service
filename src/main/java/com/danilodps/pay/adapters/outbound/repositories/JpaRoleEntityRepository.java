package com.danilodps.pay.adapters.outbound.repositories;

import com.danilodps.pay.domain.model.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaRoleEntityRepository extends JpaRepository<RoleEntity, Long> {

    @Query(value = """
        SELECT r.* FROM tb_roles r
        INNER JOIN tb_profile_roles pr ON pr.role_id = r.role_id
        WHERE pr.profile_id = :profileId
        """, nativeQuery = true)
    List<RoleEntity> findRolesByProfileId(@Param("profileId") String profileId);

}