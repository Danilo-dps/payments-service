package pay.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tb_store",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email", name = "uk_store_email"),
                @UniqueConstraint(columnNames = "cnpj", name = "uk_store_cnpj")
        }
)
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID storeId;

    @Column(nullable = false, length = 100)
    private String storeName;

    @Column(nullable = false, unique = true, length = 100)
    private String cnpj;

    @Column(nullable = false, unique = true, length = 50)
    private String storeEmail;

    @Column(nullable = false, length = 80)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "store_roles",
            joinColumns = @JoinColumn(name = "store_id", referencedColumnName = "storeId"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Builder.Default
    private Set<Role> role = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ReceivedTransferHistory> receivedTransferHistory = new ArrayList<>();

    public Store(UUID storeId, String storeName, String cnpj, String storeEmail, String password, Set<Role> role, BigDecimal balance) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.cnpj = cnpj;
        this.storeEmail = storeEmail;
        this.password = password;
        this.role = role;
        this.balance = BigDecimal.ZERO;
    }

    public Store(String storeName, String storeEmail, String password) {
        this.storeName = storeName;
        this.storeEmail = storeEmail;
        this.password = password;
    }

}