package com.danilodps.pay.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        name = "TB_STORE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "STORE_EMAIL", name = "uk_store_email"),
                @UniqueConstraint(columnNames = "STORE_CNPJ", name = "uk_store_cnpj")
        }
)
@EqualsAndHashCode(of = "storeId")
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "STORE_ID", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID storeId;

    @Column(name = "STORE_NAME", nullable = false, length = 100)
    private String storeName;

    @Column(name = "STORE_CNPJ", nullable = false, unique = true, length = 18, updatable = false)
    @ToString.Exclude
    private String cnpj;

    @Column(name = "STORE_EMAIL", nullable = false, unique = true, length = 50)
    private String storeEmail;

    @Column(name = "ACCESS_HASH", nullable = false, length = 80)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column(name = "ACCOUNT_BALANCE", nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "STORE_ROLES",
            joinColumns = @JoinColumn(name = "STORE_ID", referencedColumnName = "STORE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID"))
    @Builder.Default
    private Set<Role> role = new HashSet<>();
}