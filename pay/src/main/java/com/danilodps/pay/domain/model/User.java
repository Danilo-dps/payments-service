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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "TB_USERS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "USER_EMAIL", name = "uk_user_email"),
                @UniqueConstraint(columnNames = "DOCUMENT_NUMBER", name = "uk_user_cpf")
        }
)
@EqualsAndHashCode(of = "userId")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "USER_ID", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    @Column(name = "DOCUMENT_NUMBER", nullable = false, unique = true, length = 14, updatable = false)
    @ToString.Exclude
    private String cpf;

    @Column(name = "USER_EMAIL", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "ACCESS_HASH", nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column(name = "ACCOUNT_BALANCE", nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID"))
    @Builder.Default
    private Set<Role> role = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Deposit> deposit = new ArrayList<>();

}