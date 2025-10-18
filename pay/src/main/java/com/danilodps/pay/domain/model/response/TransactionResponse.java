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

    private UUID transactionId;
    @NotBlank(message = "amount é obrigatório")
    private BigDecimal amount;
    private LocalDateTime transactionTimestamp;
    @NotBlank(message = "userSenderEmail é obrigatório")
    private String userSenderEmail;
    @NotBlank(message = "receiverEmail é obrigatório")
    private String receiverEmail;
    @NotBlank(message = "userSenderName é obrigatório")
    private String userSenderName;
    @NotBlank(message = "receiverName é obrigatório")
    private String receiverName;

}