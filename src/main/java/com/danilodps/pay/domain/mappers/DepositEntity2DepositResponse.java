package com.danilodps.pay.domain.mappers;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.entities.DepositEntity;
import com.danilodps.pay.domain.model.entities.ProfileEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DepositEntity2DepositResponse {

    private DepositEntity2DepositResponse(){}

    public static DepositResponse convert(DepositEntity deposit, ProfileEntity profileEntity){
        return new DepositResponse(
                deposit.getDepositId(),
                profileEntity.getUsername(),
                profileEntity.getProfileEmail(),
                deposit.getAmount(),
                deposit.getDepositAt());
    }

}
