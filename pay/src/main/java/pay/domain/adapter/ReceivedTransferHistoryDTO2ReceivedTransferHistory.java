package pay.domain.adapter;

import pay.domain.dto.ReceivedTransferHistoryDTO;
import pay.domain.model.ReceivedTransferHistory;

public class ReceivedTransferHistoryDTO2ReceivedTransferHistory {

    private ReceivedTransferHistoryDTO2ReceivedTransferHistory(){}

    public static ReceivedTransferHistory convertToUser(ReceivedTransferHistoryDTO receivedTransferHistoryDTO){
        return new ReceivedTransferHistory(
                receivedTransferHistoryDTO.getReceivedId(),
                receivedTransferHistoryDTO.getWhenDidItHappen(),
                receivedTransferHistoryDTO.getFromEmail(),
                receivedTransferHistoryDTO.getOperationType(),
                receivedTransferHistoryDTO.getAmount(),
                UserDTO2User.convert(receivedTransferHistoryDTO.getUserDTO()));
    }

    public static ReceivedTransferHistory convertToStore(ReceivedTransferHistoryDTO receivedTransferHistoryDTO){
        return new ReceivedTransferHistory(
                receivedTransferHistoryDTO.getReceivedId(),
                receivedTransferHistoryDTO.getWhenDidItHappen(),
                receivedTransferHistoryDTO.getFromEmail(),
                receivedTransferHistoryDTO.getOperationType(),
                receivedTransferHistoryDTO.getAmount(),
                StoreDTO2Store.convert(receivedTransferHistoryDTO.getStoreDTO()));
    }
}
