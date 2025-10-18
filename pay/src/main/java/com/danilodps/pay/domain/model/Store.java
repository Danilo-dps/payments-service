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
        name = "tb_store",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email", name = "uk_store_email"),
                @UniqueConstraint(columnNames = "cnpj", name = "uk_store_cnpj")
        }
)
@EqualsAndHashCode(of = "storeId")
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID storeId;

    @Column(nullable = false, length = 100)
    private String storeName;

    @Column(nullable = false, unique = true, length = 18, updatable = false)
    @ToString.Exclude
    private String cnpj;

    @Column(nullable = false, unique = true, length = 50)
    private String storeEmail;

    @Column(nullable = false, length = 80)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "store_roles",
            joinColumns = @JoinColumn(name = "store_id", referencedColumnName = "storeId"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Builder.Default
    private Set<Role> role = new HashSet<>();

}