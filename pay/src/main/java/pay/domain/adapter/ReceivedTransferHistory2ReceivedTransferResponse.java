package pay.domain.adapter;

import pay.domain.model.ReceivedTransferHistory;
import pay.domain.record.ReceivedTransferResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ReceivedTransferHistory2ReceivedTransferResponse {

    private ReceivedTransferHistory2ReceivedTransferResponse(){}

    public static ReceivedTransferResponse convert(ReceivedTransferHistory receivedTransferHistory){
        return  new ReceivedTransferResponse(
                receivedTransferHistory.getReceivedId(),
                receivedTransferHistory.getWhenDidItHappen(),
                receivedTransferHistory.getFromEmail(),
                receivedTransferHistory.getAmount());
    }

    public static List<ReceivedTransferResponse> convertToList(List<ReceivedTransferHistory> listReceivedTransferHistory){
        return listReceivedTransferHistory.stream()
                .map(ReceivedTransferHistory2ReceivedTransferResponse::convert)
                .collect(Collectors.toList());
    }
}
