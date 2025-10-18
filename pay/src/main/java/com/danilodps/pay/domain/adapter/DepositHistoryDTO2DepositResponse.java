package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.DepositHistoryDTO;
import com.danilodps.pay.domain.model.response.DepositResponse;

public class DepositHistoryDTO2DepositResponse {

    private DepositHistoryDTO2DepositResponse() {}

    public static DepositResponse convert(DepositHistoryDTO depositHistoryDTO){
        return new DepositResponse (
                depositHistoryDTO.getDepositId(),
                depositHistoryDTO.getUserDTO().getUsername(),
                depositHistoryDTO.getUserDTO().getEmail(),
                depositHistoryDTO.getAmount(),
                depositHistoryDTO.getDepositTimestamp());
    }
}
