package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.RoleEntity;
import com.danilodps.pay.domain.model.enums.RoleEnum;

import java.util.List;
import java.util.stream.Collectors;

public class RoleEnum2RoleEntity {

    private RoleEnum2RoleEntity(){}

    public static RoleEntity convert(RoleEnum roleEnum){
        return RoleEntity.builder()
                .roleId(roleEnum.getId())
                .shortName(roleEnum.getShortName())
                .description(roleEnum.getDescription())
                .build();
    }

    public static List<RoleEntity> convertList(List<RoleEnum> roleEnums){
        return roleEnums.stream()
                .map(RoleEnum2RoleEntity::convert)
                .collect(Collectors.toList());
    }

    public static RoleEntity convert(String documentType){
        RoleEnum roleEnum = RoleEnum.getByShortName(documentType);
        return RoleEntity.builder()
                .roleId(roleEnum.getId())
                .shortName(roleEnum.getShortName())
                .description(roleEnum.getDescription())
                .build();
    }

}
