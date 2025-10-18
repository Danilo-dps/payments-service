package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.Deposit;
import com.danilodps.pay.domain.model.response.DepositResponse;

import java.util.List;
import java.util.stream.Collectors;

public class Deposit2DepositResponse {

    private Deposit2DepositResponse(){}

    public static DepositResponse convert(Deposit deposit){
        return new DepositResponse(deposit.getDepositId(), deposit.getUser().getUsername(), deposit.getUser().getEmail(), deposit.getAmount(), deposit.getDepositTimestamp());
    }

    public static List<DepositResponse> convertToList(List<Deposit> listDeposit){
        return listDeposit.stream()
                .map(Deposit2DepositResponse::convert)
                .collect(Collectors.toList());
    }
}
