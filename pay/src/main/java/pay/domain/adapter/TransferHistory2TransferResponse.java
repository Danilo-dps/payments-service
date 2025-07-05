package pay.domain.adapter;

import pay.domain.model.TransferHistory;
import pay.domain.record.TransferResponse;

import java.util.List;
import java.util.stream.Collectors;

public class TransferHistory2TransferResponse {

    private TransferHistory2TransferResponse(){}

    public static TransferResponse convert(TransferHistory transferHistory){
        return new TransferResponse(transferHistory.getTransferId(), transferHistory.getUser().getUsername(), transferHistory.getUser().getEmail(), transferHistory.getDestinationEmail(), transferHistory.getAmount(), transferHistory.getWhenDidItHappen());
    }

    public static List<TransferResponse> convertToList(List<TransferHistory> listTransferHistory){
        return listTransferHistory.stream()
                .map(TransferHistory2TransferResponse::convert)
                .collect(Collectors.toList());
    }
}
