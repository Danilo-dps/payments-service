package pay.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pay.domain.model.enums.EOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SentTransferHistoryDTO {

    private UUID sendId;
    private LocalDateTime whenDidItHappen;
    private String destinationEmail;
    private EOperationType operationType;
    private BigDecimal amount;
    private UserDTO userDTO;

    public SentTransferHistoryDTO(LocalDateTime whenDidItHappen, String destinationEmail, EOperationType operationType, BigDecimal amount, UserDTO userDTO) {
        this.whenDidItHappen = whenDidItHappen;
        this.destinationEmail = destinationEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.userDTO = userDTO;
    }

}


