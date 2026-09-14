package com.danilodps.pay.domain.mappers.entities.jpa;

import com.danilodps.pay.adapters.outbound.entities.JpaDepositEntity;
import com.danilodps.pay.domain.model.DepositEntity;

import java.util.List;
import java.util.stream.Collectors;

public class JpaDepositEntity2DepositEntity {

    private JpaDepositEntity2DepositEntity(){}

    public static DepositEntity convert(JpaDepositEntity jpaDepositEntity){
        return new DepositEntity(
                jpaDepositEntity.getDepositId(),
                jpaDepositEntity.getDepositAt(),
                jpaDepositEntity.getAmount(),
                JpaProfileEntity2ProfileEntity.convert(jpaDepositEntity.getProfileEntity()));
    }

    public static List<DepositEntity> convert(List<JpaDepositEntity> jpaDepositEntities) {
        return jpaDepositEntities.stream()
                .map(JpaDepositEntity2DepositEntity::convert)
                .collect(Collectors.toList());
    }

}
