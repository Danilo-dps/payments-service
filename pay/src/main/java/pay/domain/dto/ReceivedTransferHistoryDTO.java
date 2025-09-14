package pay.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pay.domain.model.enums.EOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReceivedTransferHistoryDTO {

    private UUID receivedId;
    private LocalDateTime whenDidItHappen;
    private String fromEmail;
    private EOperationType operationType;
    private BigDecimal amount;
    private UserDTO userDTO;
    private StoreDTO storeDTO;

    public ReceivedTransferHistoryDTO(UUID receivedId, LocalDateTime whenDidItHappen, String fromEmail, EOperationType operationType, BigDecimal amount, UserDTO userDTO) {
        this.receivedId = receivedId;
        this.whenDidItHappen = whenDidItHappen;
        this.fromEmail = fromEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.userDTO = userDTO;
    }

    public ReceivedTransferHistoryDTO(UUID receivedId, LocalDateTime whenDidItHappen, String fromEmail, EOperationType operationType, BigDecimal amount, StoreDTO storeDTO) {
        this.receivedId = receivedId;
        this.whenDidItHappen = whenDidItHappen;
        this.fromEmail = fromEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.storeDTO = storeDTO;
    }
}
