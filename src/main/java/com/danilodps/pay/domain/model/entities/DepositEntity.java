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
@Table(name = "TB_DEPOSIT")
@EqualsAndHashCode(of = "depositId")
public class DepositEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DEPOSIT_ID")
    private String depositId;

    @Column(name = "DEPOSIT_AT")
    private LocalDateTime depositAt;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "PROFILE_ID", updatable = false)
    private String profileId;

}