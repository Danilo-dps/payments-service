package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.DepositDTO;
import com.danilodps.pay.domain.model.response.DepositResponse;

public class DepositDTO2DepositResponse {

    private DepositDTO2DepositResponse() {}

    public static DepositResponse convert(DepositDTO depositDTO){
        return new DepositResponse (
                depositDTO.getDepositId(),
                depositDTO.getUserDTO().getUsername(),
                depositDTO.getUserDTO().getEmail(),
                depositDTO.getAmount(),
                depositDTO.getDepositTimestamp());
    }
}
