package com.danilodps.pay.domain.adapter;

import com.danilodps.pay.domain.dto.DepositHistoryDTO;
import com.danilodps.pay.domain.model.DepositHistory;

public class DepositHistory2DepositHistoryDTO {

    private DepositHistory2DepositHistoryDTO() {}

    public static DepositHistoryDTO convert(DepositHistory depositHistory){
        return new DepositHistoryDTO(depositHistory.getDepositId(),
                depositHistory.getDepositTimestamp(),
                depositHistory.getOperationType(),
                depositHistory.getAmount(),
                User2UserDTO.convert(depositHistory.getUser()));
    }
}
