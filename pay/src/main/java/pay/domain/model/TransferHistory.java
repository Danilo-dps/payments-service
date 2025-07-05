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
@Table(name = "tb_transfer_history")
public class TransferHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID transferId;
    private LocalDateTime whenDidItHappen;
    private String destinationEmail;
    private String operationType;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public TransferHistory(LocalDateTime whenDidItHappen, String destinationEmail, String operationType, BigDecimal amount, User user) {
        this.whenDidItHappen = whenDidItHappen;
        this.destinationEmail = destinationEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.user = user;
    }
}