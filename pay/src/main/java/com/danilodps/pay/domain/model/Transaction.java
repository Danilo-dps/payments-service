package com.danilodps.pay.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_TRANSACTIONS")
public class Transaction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TRANSACTION_ID", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "TRANSACTION_TIMESTAMP", nullable = false)
    private LocalDateTime transactionTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_USER_ID", nullable = false)
    private User userSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_USER_ID")
    private User userReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_STORE_ID")
    private Store storeReceiver;

    public boolean hasValidReceiver() {
        return userReceiver != null || storeReceiver != null;
    }

    public boolean isValidTransaction() {
        if (userReceiver != null && userSender != null) {
            return !userSender.getUserId().equals(userReceiver.getUserId());
        }
        return true;
    }
}