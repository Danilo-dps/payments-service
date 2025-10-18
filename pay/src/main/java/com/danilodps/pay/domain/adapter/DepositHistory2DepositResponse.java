package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.model.DepositHistory;
import com.danilodps.pay.domain.model.response.DepositResponse;

import java.util.List;
import java.util.stream.Collectors;

public class DepositHistory2DepositResponse {

    private DepositHistory2DepositResponse(){}

    public static DepositResponse convert(DepositHistory depositHistory){
        return new DepositResponse(depositHistory.getDepositId(), depositHistory.getUser().getUsername(), depositHistory.getUser().getEmail(),depositHistory.getAmount(), depositHistory.getDepositTimestamp());
    }

    public static List<DepositResponse> convertToList(List<DepositHistory> listDepositHistory){
        return listDepositHistory.stream()
                .map(DepositHistory2DepositResponse::convert)
                .collect(Collectors.toList());
    }
}
