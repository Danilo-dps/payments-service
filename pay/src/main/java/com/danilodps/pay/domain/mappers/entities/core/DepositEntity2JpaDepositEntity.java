package com.danilodps.pay.domain.mappers.entities.core;

import com.danilodps.pay.adapters.outbound.entities.JpaDepositEntity;
import com.danilodps.pay.domain.model.DepositEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DepositEntity2JpaDepositEntity {

    private DepositEntity2JpaDepositEntity(){}

    public static JpaDepositEntity convert(DepositEntity depositEntity){
        return new JpaDepositEntity(
                depositEntity.getDepositId(),
                depositEntity.getDepositAt(),
                depositEntity.getAmount(),
                ProfileEntity2JpaProfileEntity.convert(depositEntity.getProfileEntity()));
    }

    public static List<JpaDepositEntity> convert(List<DepositEntity> depositEntities) {
        return depositEntities.stream()
                .map(DepositEntity2JpaDepositEntity::convert)
                .collect(Collectors.toList());
    }

}
