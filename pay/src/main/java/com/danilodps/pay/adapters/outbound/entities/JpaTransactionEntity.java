package com.danilodps.pay.adapters.outbound.entities;

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
public class JpaTransactionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TRANSACTION_ID", updatable = false, nullable = false)
    private String transactionId;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "TRANSACTION_AT", nullable = false)
    private LocalDateTime transactionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_PROFILE_ID", nullable = false)
    private JpaProfileEntity profileSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_PROFILE_ID", nullable = false)
    private JpaProfileEntity profileReceiver;

}