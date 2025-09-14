package pay.domain.adapter;

import pay.domain.dto.ReceivedTransferHistoryDTO;
import pay.domain.model.ReceivedTransferHistory;

public class ReceivedTransferHistory2ReceivedTransferHistoryDTO {

    private ReceivedTransferHistory2ReceivedTransferHistoryDTO(){}

    public static ReceivedTransferHistoryDTO convertToUserDTO(ReceivedTransferHistory receivedTransferHistory){
        return new ReceivedTransferHistoryDTO(
                receivedTransferHistory.getReceivedId(),
                receivedTransferHistory.getWhenDidItHappen(),
                receivedTransferHistory.getFromEmail(),
                receivedTransferHistory.getOperationType(),
                receivedTransferHistory.getAmount(),
                User2UserDTO.convert(receivedTransferHistory.getUser()));
    }

    public static ReceivedTransferHistoryDTO convertToStoreDTO(ReceivedTransferHistory receivedTransferHistory){
        return new ReceivedTransferHistoryDTO(
                receivedTransferHistory.getReceivedId(),
                receivedTransferHistory.getWhenDidItHappen(),
                receivedTransferHistory.getFromEmail(),
                receivedTransferHistory.getOperationType(),
                receivedTransferHistory.getAmount(),
                Store2StoreDTO.convert(receivedTransferHistory.getStore()));
    }
}
