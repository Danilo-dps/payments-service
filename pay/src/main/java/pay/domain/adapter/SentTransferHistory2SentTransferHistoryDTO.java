package pay.domain.adapter;

import pay.domain.dto.SentTransferHistoryDTO;
import pay.domain.model.SentTransferHistory;

public class SentTransferHistory2SentTransferHistoryDTO {
    private SentTransferHistory2SentTransferHistoryDTO(){}

    public static SentTransferHistoryDTO convert(SentTransferHistory sentTransferHistory){
        return new SentTransferHistoryDTO(
                sentTransferHistory.getSentId(),
                sentTransferHistory.getWhenDidItHappen(),
                sentTransferHistory.getDestinationEmail(),
                sentTransferHistory.getOperationType(),
                sentTransferHistory.getAmount(),
                User2UserDTO.convert(sentTransferHistory.getUser())
        );
    }
}
