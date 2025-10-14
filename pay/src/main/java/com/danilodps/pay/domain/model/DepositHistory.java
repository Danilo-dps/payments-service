package com.danilodps.pay.domain.model;

import com.danilodps.pay.domain.model.enums.EOperationType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_deposit_history")
public class DepositHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String depositId;
    private LocalDateTime whenDidItHappen;
    private EOperationType operationType;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public DepositHistory(LocalDateTime whenDidItHappen, EOperationType operationType, BigDecimal amount, User user) {
        this.whenDidItHappen = whenDidItHappen;
        this.operationType = operationType;
        this.amount = amount;
        this.user = user;
    }
}