package com.danilodps.pay.domain.model;

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
@Table(name = "TB_DEPOSIT")
@EqualsAndHashCode(of = "depositId")
public class DepositEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DEPOSIT_ID", updatable = false, nullable = false)
    private String depositId;

    @Column(name = "DEPOSIT_TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime depositTimestamp;

    @Column(name = "AMOUNT", nullable = false, updatable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private ProfileEntity profileEntity;

}