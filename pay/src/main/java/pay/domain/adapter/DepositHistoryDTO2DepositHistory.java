package pay.domain.adapter;

import pay.domain.dto.DepositHistoryDTO;
import pay.domain.model.DepositHistory;

public class DepositHistoryDTO2DepositHistory {

    private DepositHistoryDTO2DepositHistory() {}

    public static DepositHistory convert(DepositHistoryDTO depositHistoryDTO){
        return new DepositHistory(depositHistoryDTO.getDepositId(),
                depositHistoryDTO.getWhenDidItHappen(),
                depositHistoryDTO.getOperationType(),
                depositHistoryDTO.getAmount(),
                UserDTO2User.convert(depositHistoryDTO.getUserDTO()));
    }
}
