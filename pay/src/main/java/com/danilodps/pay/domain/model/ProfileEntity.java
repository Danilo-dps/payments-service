package com.danilodps.pay.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "TB_PROFILE_ENTITY",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "PROFILE_EMAIL", name = "uk_profile_email"),
                @UniqueConstraint(columnNames = "DOCUMENT_NUMBER", name = "uk_profile")
        }
)
@EqualsAndHashCode(of = "profileId")
public class ProfileEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PROFILE_ID", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID profileId;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    @Column(name = "DOCUMENT_TYPE", nullable = false, updatable = false)
    @ToString.Exclude
    private DocumentTypeEntity documentType;

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