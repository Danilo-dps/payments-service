package pay.domain.model;

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
@Table(name = "tb_deposit_history")
public class DepositHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID depositId;
    private LocalDateTime whenDidItHappen;
    private String operationType;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public DepositHistory(LocalDateTime whenDidItHappen, String operationType, BigDecimal amount, User user) {
        this.whenDidItHappen = whenDidItHappen;
        this.operationType = operationType;
        this.amount = amount;
        this.user = user;
    }
}