package com.danilodps.pay.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_TRANSACTION")
public class TransactionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "TRANSACTION_AT")
    private LocalDateTime transactionAt;

    @Column(name = "SENDER_PROFILE_ID")
    private String senderProfileId;

    @Column(name = "RECEIVER_PROFILE_ID")
    private String receiverProfileId;

}