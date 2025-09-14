package pay.domain.adapter;

import pay.domain.model.SentTransferHistory;
import pay.domain.record.SentTransferResponse;

import java.util.List;
import java.util.stream.Collectors;

public class SentTransferHistory2SentTransferResponse {

    private SentTransferHistory2SentTransferResponse(){}

    public static SentTransferResponse convert(SentTransferHistory sentTransferHistory){
        return  new SentTransferResponse(
                sentTransferHistory.getSentId(),
                sentTransferHistory.getWhenDidItHappen(),
                sentTransferHistory.getDestinationEmail(),
                sentTransferHistory.getAmount());
    }

    public static List<SentTransferResponse> convertToList(List<SentTransferHistory> listSentTransferHistory){
        return listSentTransferHistory.stream()
                .map(SentTransferHistory2SentTransferResponse::convert)
                .collect(Collectors.toList());
    }
}
