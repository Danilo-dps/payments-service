package com.danilodps.pay.domain.adapter;

import com.danilodps.domain.model.response.DepositResponse;
import com.danilodps.pay.domain.model.DepositEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DepositEntity2DepositResponse {

    private DepositEntity2DepositResponse(){}

    public static DepositResponse convert(DepositEntity deposit){
        return DepositResponse.builder()
                .depositId(deposit.getDepositId())
                .username(deposit.getProfileEntity().getUsername())
                .userEmail(deposit.getProfileEntity().getProfileEmail())
                .amount(deposit.getAmount())
                .depositTimestamp(deposit.getDepositTimestamp())
                .build();
    }

    public static List<DepositResponse> convertToList(List<DepositEntity> listDeposit){
        return listDeposit.stream()
                .map(DepositEntity2DepositResponse::convert)
                .collect(Collectors.toList());
    }
}
