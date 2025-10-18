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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_deposit_history")
@EqualsAndHashCode(of = "depositId")
public class DepositHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID depositId;

    @Column(name = "deposit_timestamp", nullable = false, updatable = false)
    private LocalDateTime depositTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, updatable = false)
    private EOperationType operationType;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public DepositHistory(LocalDateTime depositTimestamp, EOperationType operationType, BigDecimal amount, User user) {
        this.depositTimestamp = depositTimestamp;
        this.operationType = operationType;
        this.amount = amount;
        this.user = user;
    }
}