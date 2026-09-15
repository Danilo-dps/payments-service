package com.danilodps.pay.domain.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_PROFILE")
@EqualsAndHashCode(of = "profileId")
public class ProfileEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PROFILE_ID")
    private String profileId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "DOCUMENT_IDENTIFIER")
    private String documentIdentifier;

    @Column(name = "DOCUMENT")
    private String document;

    @Column(name = "PROFILE_EMAIL")
    private String profileEmail;

    @Column(name = "ACCESS_HASH")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String password;

    @Column(name = "ACCOUNT_BALANCE")
    private BigDecimal balance;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;

}