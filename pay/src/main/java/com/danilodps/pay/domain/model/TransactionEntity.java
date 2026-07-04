package com.danilodps.pay.domain.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String transactionId;

    private BigDecimal amount;

    private LocalDateTime transactionAt;

    private ProfileEntity profileSender;

    private ProfileEntity profileReceiver;

    public TransactionEntity() {
    }

    public TransactionEntity(String transactionId,
                             BigDecimal amount,
                             LocalDateTime transactionAt,
                             ProfileEntity profileSender,
                             ProfileEntity profileReceiver) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionAt = transactionAt;
        this.profileSender = profileSender;
        this.profileReceiver = profileReceiver;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionAt() {
        return transactionAt;
    }

    public void setTransactionAt(LocalDateTime transactionAt) {
        this.transactionAt = transactionAt;
    }

    public ProfileEntity getProfileSender() {
        return profileSender;
    }

    public void setProfileSender(ProfileEntity profileSender) {
        this.profileSender = profileSender;
    }

    public ProfileEntity getProfileReceiver() {
        return profileReceiver;
    }

    public void setProfileReceiver(ProfileEntity profileReceiver) {
        this.profileReceiver = profileReceiver;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionEntity that)) return false;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionId);
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", transactionAt=" + transactionAt +
                ", profileSender=" + profileSender +
                ", profileReceiver=" + profileReceiver +
                '}';
    }

}