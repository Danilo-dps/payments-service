package com.danilodps.pay.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class DepositEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String depositId;

    private LocalDateTime depositAt;

    private BigDecimal amount;

    private ProfileEntity profileEntity;

    public DepositEntity() {
    }

    public DepositEntity(String depositId,
                         LocalDateTime depositAt,
                         BigDecimal amount,
                         ProfileEntity profileEntity) {
        this.depositId = depositId;
        this.depositAt = depositAt;
        this.amount = amount;
        this.profileEntity = profileEntity;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public LocalDateTime getDepositAt() {
        return depositAt;
    }

    public void setDepositAt(LocalDateTime depositAt) {
        this.depositAt = depositAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ProfileEntity getProfileEntity() {
        return profileEntity;
    }

    public void setProfileEntity(ProfileEntity profileEntity) {
        this.profileEntity = profileEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DepositEntity that)) return false;
        return Objects.equals(depositId, that.depositId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(depositId);
    }

    @Override
    public String toString() {
        return "DepositEntity{" +
                "depositId='" + depositId + '\'' +
                ", depositAt=" + depositAt +
                ", amount=" + amount +
                ", profileEntity=" + profileEntity +
                '}';
    }

}