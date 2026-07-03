package com.danilodps.pay.domain.mappers.entities;

import com.danilodps.pay.adapters.outbound.entities.JpaRoleEntity;
import com.danilodps.pay.domain.model.RoleEntity;

import java.util.List;
import java.util.stream.Collectors;

public class JpaRoleEntity2RoleEntity {

    private JpaRoleEntity2RoleEntity(){}

    public static RoleEntity convert(JpaRoleEntity jpaRoleEntity){
        return new RoleEntity(jpaRoleEntity.getRoleId(),
                jpaRoleEntity.getDocIdentifier(),
                jpaRoleEntity.getRoleGrantedAuthority(),
                jpaRoleEntity.getDescription());
    }

    public static List<RoleEntity>  convert(List<JpaRoleEntity> jpaRoleEntities){
        return jpaRoleEntities.stream()
                .map(JpaRoleEntity2RoleEntity::convert)
                .collect(Collectors.toList());
    }

}
