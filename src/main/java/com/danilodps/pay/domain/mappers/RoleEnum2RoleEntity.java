package com.danilodps.pay.domain.mappers;

import com.danilodps.pay.domain.model.RoleEntity;
import com.danilodps.pay.domain.model.enums.RoleEnum;

import java.util.List;
import java.util.stream.Collectors;

public class RoleEnum2RoleEntity {

    private RoleEnum2RoleEntity(){}

    public static RoleEntity convert(RoleEnum roleEnum){
        return new RoleEntity(roleEnum.getId(), roleEnum.getDocIdentifier(), roleEnum.getRoleGrantedAuthority(), roleEnum.getDescription());
    }

    public static List<RoleEntity> convertList(List<RoleEnum> roleEnums){
        return roleEnums.stream()
                .map(RoleEnum2RoleEntity::convert)
                .collect(Collectors.toList());
    }

    public static RoleEntity convert(String documentType){
        RoleEnum roleEnum = RoleEnum.getByShortName(documentType);
        return new RoleEntity(roleEnum.getId(), roleEnum.getDocIdentifier(), roleEnum.getRoleGrantedAuthority(), roleEnum.getDescription());
    }

}
