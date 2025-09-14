package pay.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pay.domain.model.enums.EOperationType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "tb_received_history")
public class ReceivedTransferHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID receivedId;
    private LocalDateTime whenDidItHappen;
    private String fromEmail;
    private EOperationType operationType;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public ReceivedTransferHistory(UUID receivedId, LocalDateTime whenDidItHappen, String fromEmail, EOperationType operationType, BigDecimal amount, User user) {
        this.receivedId = receivedId;
        this.whenDidItHappen = whenDidItHappen;
        this.fromEmail = fromEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.user = user;
    }

    public ReceivedTransferHistory(UUID receivedId, LocalDateTime whenDidItHappen, String fromEmail, EOperationType operationType, BigDecimal amount, Store store) {
        this.receivedId = receivedId;
        this.whenDidItHappen = whenDidItHappen;
        this.fromEmail = fromEmail;
        this.operationType = operationType;
        this.amount = amount;
        this.store = store;
    }
}