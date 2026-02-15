package com.danilodps.pay.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "TB_PROFILE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "PROFILE_EMAIL", name = "uk_profile_email"),
                @UniqueConstraint(columnNames = "DOCUMENT", name = "uk_document")
        }
)
@EqualsAndHashCode(of = "profileId")
public class ProfileEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PROFILE_ID", updatable = false, nullable = false)
    private String profileId;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    @Column(name = "DOCUMENT_IDENTIFIER", nullable = false, unique = true, length = 4)
    private String documentIdentifier;

    @Column(name = "DOCUMENT", nullable = false, unique = true, length = 18)
    private String document;

    @Column(name = "PROFILE_EMAIL", nullable = false, unique = true, length = 50)
    private String profileEmail;

    @Column(name = "ACCESS_HASH", nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column(name = "ACCOUNT_BALANCE", nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "PROFILE_ROLES",
            joinColumns = @JoinColumn(name = "PROFILE_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @Builder.Default
    private List<RoleEntity> roles = new ArrayList<>();

}