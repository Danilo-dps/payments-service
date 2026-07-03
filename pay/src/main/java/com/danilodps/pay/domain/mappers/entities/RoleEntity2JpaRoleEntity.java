package com.danilodps.pay.domain.mappers.entities;

import com.danilodps.pay.adapters.outbound.entities.JpaRoleEntity;
import com.danilodps.pay.domain.model.RoleEntity;

import java.util.List;
import java.util.stream.Collectors;

public class RoleEntity2JpaRoleEntity {

    private RoleEntity2JpaRoleEntity(){}

    public static JpaRoleEntity convert(RoleEntity roleEntity){
        return new JpaRoleEntity(roleEntity.getRoleId(),
                roleEntity.getDocIdentifier(),
                roleEntity.getRoleGrantedAuthority(),
                roleEntity.getDescription());
    }

    public static List<JpaRoleEntity>  convert(List<RoleEntity> roleEntities){
        return roleEntities.stream()
                .map(RoleEntity2JpaRoleEntity::convert)
                .collect(Collectors.toList());
    }

}
