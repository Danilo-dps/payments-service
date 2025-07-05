package pay.domain.adapter;

import pay.domain.dto.TransferHistoryDTO;
import pay.domain.model.TransferHistory;

public class TransferHistory2TransferHistoryDTO {

    private TransferHistory2TransferHistoryDTO() {}

    public static TransferHistoryDTO convert(TransferHistory transferHistory){
        return new TransferHistoryDTO(transferHistory.getTransferId(),
                transferHistory.getWhenDidItHappen(),
                transferHistory.getDestinationEmail(),
                transferHistory.getOperationType(),
                transferHistory.getAmount(),
                User2UserDTO.convert(transferHistory.getUser()));
    }
}
