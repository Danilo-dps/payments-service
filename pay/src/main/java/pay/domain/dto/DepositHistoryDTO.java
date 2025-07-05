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
public class DepositHistoryDTO {

    private UUID depositId;
    private LocalDateTime whenDidItHappen;
    private String operationType;
    private BigDecimal amount;
    private UserDTO userDTO;

    public DepositHistoryDTO(LocalDateTime whenDidItHappen, String operationType, BigDecimal amount, UserDTO userDTO) {
        this.whenDidItHappen = whenDidItHappen;
        this.operationType = operationType;
        this.amount = amount;
        this.userDTO = userDTO;
    }
}