package pay.domain.adapter;

import pay.domain.dto.TransferHistoryDTO;
import pay.domain.model.TransferHistory;

public class TransferHistoryDTO2TransferHistory {

    private TransferHistoryDTO2TransferHistory() {}

    public static TransferHistory convert(TransferHistoryDTO transferHistoryDTO){
        return new TransferHistory(transferHistoryDTO.getTransferId(),
                transferHistoryDTO.getWhenDidItHappen(),
                transferHistoryDTO.getDestinationEmail(),
                transferHistoryDTO.getOperationType(),
                transferHistoryDTO.getAmount(),
                UserDTO2User.convert(transferHistoryDTO.getUserDTO()));
    }
}
