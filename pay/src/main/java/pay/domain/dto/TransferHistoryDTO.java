package pay.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransferHistoryDTO {

    private UUID transferId;
    private LocalDateTime whenDidItHappen;
    private String destinationEmail;
    private String operationType;
    private BigDecimal amount;
    private UserDTO userDTO;

    public TransferHistoryDTO(LocalDateTime whenDidItHappen, String destinationEmail, String operationType, BigDecimal amount, UserDTO userDTO) {
        this.whenDidItHappen = whenDidItHappen;
        this.destinationEmail = destinationEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.userDTO = userDTO;
    }
}