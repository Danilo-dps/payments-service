package com.danilodps.pay.domain.model;

import com.danilodps.pay.domain.model.enums.EOperationType;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_DEPOSIT")
@EqualsAndHashCode(of = "depositId")
public class Deposit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DEPOSIT_ID", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID depositId;

    @Column(name = "DEPOSIT_TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime depositTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPERATION_TYPE", nullable = false, updatable = false)
    private EOperationType operationType;

    @Column(name = "AMOUNT", nullable = false, updatable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    private User user;

    public Deposit(LocalDateTime depositTimestamp, EOperationType operationType, BigDecimal amount, User user) {
        this.depositTimestamp = depositTimestamp;
        this.operationType = operationType;
        this.amount = amount;
        this.user = user;
    }
}