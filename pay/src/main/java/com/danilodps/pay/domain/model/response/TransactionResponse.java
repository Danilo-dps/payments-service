package com.danilodps.pay.domain.model.response;

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
public class TransactionResponse {

    private String transactionId;
    @NotBlank(message = "amount é obrigatório")
    private BigDecimal amount;
    private LocalDateTime transactionTimestamp;
    @NotBlank(message = "userSender é obrigatório")
    private String userSender;
    @NotBlank(message = "receiver é obrigatório")
    private String receiver;

}