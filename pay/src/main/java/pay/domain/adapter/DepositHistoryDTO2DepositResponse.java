package pay.domain.adapter;

import pay.domain.dto.DepositHistoryDTO;
import pay.domain.model.response.DepositResponse;

public class DepositHistoryDTO2DepositResponse {

    private DepositHistoryDTO2DepositResponse() {}

    public static DepositResponse convert(DepositHistoryDTO depositHistoryDTO){
        return new DepositResponse (
                depositHistoryDTO.getDepositId(),
                depositHistoryDTO.getUserDTO().getUsername(),
                depositHistoryDTO.getUserDTO().getEmail(),
                depositHistoryDTO.getAmount(),
                depositHistoryDTO.getWhenDidItHappen());
    }
}
