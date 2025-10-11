package pay.domain.model.request;

import jakarta.validation.constraints.NotBlank;
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
public class TransactionRequest {

    private UUID transactionId;
    @NotBlank(message = "amount é obrigatório")
    private BigDecimal amount;
    private LocalDateTime transactionTimestamp;
    @NotBlank(message = "userSender é obrigatório")
    private String userSender;
    @NotBlank(message = "receiver é obrigatório")
    private String receiver;

}