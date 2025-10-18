package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.DepositDTO;
import com.danilodps.pay.domain.model.Deposit;

public class Deposit2DepositDTO {

    private Deposit2DepositDTO() {}

    public static DepositDTO convert(Deposit deposit){
        return new DepositDTO(deposit.getDepositId(),
                deposit.getDepositTimestamp(),
                deposit.getOperationType(),
                deposit.getAmount(),
                User2UserDTO.convert(deposit.getUser()));
    }
}
