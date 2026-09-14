package com.danilodps.pay.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ProfileEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String profileId;

    private String username;

    private String documentIdentifier;

    private String document;

    private String profileEmail;

    private String password;

    private BigDecimal balance;

    private List<RoleEntity> roles;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;

    public ProfileEntity() {
    }

    public ProfileEntity(String profileId,
                         String username,
                         String documentIdentifier,
                         String document,
                         String profileEmail,
                         String password,
                         BigDecimal balance,
                         List<RoleEntity> roles,
                         LocalDateTime createdAt,
                         LocalDateTime lastUpdated) {
        this.profileId = profileId;
        this.username = username;
        this.documentIdentifier = documentIdentifier;
        this.document = document;
        this.profileEmail = profileEmail;
        this.password = password;
        this.balance = balance;
        this.roles = roles;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProfileEntity that)) return false;
        return Objects.equals(profileId, that.profileId) && Objects.equals(profileEmail, that.profileEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, profileEmail);
    }

    @Override
    public String toString() {
        return "ProfileEntity{" +
                "profileId='" + profileId + '\'' +
                ", username='" + username + '\'' +
                ", documentIdentifier='" + documentIdentifier + '\'' +
                ", document='" + document + '\'' +
                ", profileEmail='" + profileEmail + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                ", roles=" + roles +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}